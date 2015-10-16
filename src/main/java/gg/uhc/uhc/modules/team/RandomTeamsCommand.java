package gg.uhc.uhc.modules.team;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.IntegerConverter;
import gg.uhc.flagcommands.converters.OfflinePlayerConverter;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.IntegerPredicates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RandomTeamsCommand extends OptionCommand {

    protected static final String TEAMUP_NOTIFICATION = ChatColor.AQUA + "You were teamed up into the team %s%s " + ChatColor.RESET + ChatColor.AQUA + "with: " + ChatColor.DARK_PURPLE + "%s";
    protected static final String CREATED_TEAMS = ChatColor.AQUA + "Created %d teams of size %d from %d available players";

    protected final TeamModule module;

    protected final OptionSpec<Player> playersSpec;
    protected final OptionSpec<Integer> teamCountSpec;
    protected final OptionSpec<Integer> teamSizeSpec;
    protected final OptionSpec<OfflinePlayer> excludingSpec;

    public RandomTeamsCommand(TeamModule module) {
        this.module = module;

        playersSpec = parser
                .nonOptions("Players to put into random teams, leave empty for all players online")
                .withValuesConvertedBy(new OnlinePlayerConverter());

        teamCountSpec = parser
                .acceptsAll(ImmutableList.of("c", "count"), "How many teams to create, teams will be as even as possible. Cannot be used with -s")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter().setPredicate(IntegerPredicates.GREATER_THAN_ZERO).setType("Integer > 0"));

        teamSizeSpec = parser
                .acceptsAll(ImmutableList.of("s", "size"), "How big to attempt make each team. The final team may have less members. Cannot be used with -c")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter().setPredicate(IntegerPredicates.GREATER_THAN_ZERO).setType("Integer > 0"));

        excludingSpec = parser
                .acceptsAll(ImmutableList.of("e", "exclude"), "List of players to exclude from selection separated with commas")
                .withRequiredArg()
                .withValuesSeparatedBy(",")
                .withValuesConvertedBy(new OfflinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        int size = -1;
        int count = -1;

        if (options.has(teamCountSpec)) {
            count = teamCountSpec.value(options);
        }

        if (options.has(teamSizeSpec)) {
            size = teamSizeSpec.value(options);
        }

        if (size == -1 && count == -1) {
            sender.sendMessage(ChatColor.RED + "You must provide either `-c <number>` for team count or `-s <number>` for team size");
            return true;
        }

        if (size != -1 && count != -1) {
            sender.sendMessage(ChatColor.RED + "You must provide either `-c <number>` OR `-s <number>` not both");
            return true;
        }

        Set<Player> choice = Sets.newHashSet(playersSpec.values(options));

        // if none are provided then add all online players
        if (choice.size() == 0) {
            choice = Sets.newHashSet(Bukkit.getOnlinePlayers());
        }

        // parse excludes into online players
        Set<Player> excludes = Sets.newHashSet(
                Iterables.filter(
                        Iterables.transform(
                                excludingSpec.values(options),
                                FunctionalUtil.ONLINE_VERSION
                        ),
                        Predicates.notNull()
                )
        );

        // final list with excludes removed and players that already in a team
        List<Player> toAssign = Lists.newArrayList(
                Iterables.filter(
                        Sets.difference(choice, excludes),
                        Predicates.not(PLAYER_HAS_TEAM)
                )
        );

        if (toAssign.size() == 0) {
            sender.sendMessage(ChatColor.RED + "There are no players to add to a teams");
            return true;
        }

        Collections.shuffle(toAssign);

        // calculate team sizes to fit in count teams and assign it to size and carry on as if it was a sized command
        if (count != -1) {
            size = (int) Math.ceil((double) toAssign.size() / (double) count);
        }

        // partition into teams
        List<List<Player>> teams = Lists.partition(toAssign, size);

        // start assigning teams
        for (List<Player> teamPlayers : teams) {
            Optional<Team> optional = module.findFirstEmptyTeam();

            if (!optional.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Ran out of teams to assign players to. Clear up the teams and try again");
                return true;
            }

            Team team = optional.get();
            String playerNames = Joiner.on(", ").join(Iterables.transform(teamPlayers, FunctionalUtil.PLAYER_NAME_FETCHER));
            String message = String.format(TEAMUP_NOTIFICATION, team.getPrefix(), team.getDisplayName(), playerNames);

            // add each player
            for (Player player : teamPlayers) {
                team.addPlayer(player);
                player.sendMessage(message);
            }
        }

        sender.sendMessage(String.format(CREATED_TEAMS, teams.size(), teams.get(0).size(), toAssign.size()));
        return true;
    }

    protected final Predicate<OfflinePlayer> PLAYER_HAS_TEAM = new Predicate<OfflinePlayer>() {
        @Override
        public boolean apply(OfflinePlayer input) {
            return module.getScoreboard().getPlayerTeam(input) != null;
        }
    };
}
