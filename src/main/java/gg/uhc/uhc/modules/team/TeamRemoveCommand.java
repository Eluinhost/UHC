package gg.uhc.uhc.modules.team;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import gg.uhc.uhc.command.OptionCommand;
import gg.uhc.uhc.command.converters.OfflinePlayerConverter;
import gg.uhc.uhc.command.converters.TeamConverter;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class TeamRemoveCommand extends OptionCommand {

    protected static final String COMPLETE = ChatColor.AQUA + "Removed %d players, team is now: " + ChatColor.DARK_PURPLE + "%s";

    protected final OptionSpec<Team> teamSpec;
    protected final OptionSpec<OfflinePlayer> playersSpec;
    protected final OptionSpec<Void> removeAllSpec;

    public TeamRemoveCommand(TeamModule module) {
        teamSpec = parser
                .acceptsAll(ImmutableList.of("t", "team"), "Name of the team to remove players from")
                .withRequiredArg()
                .required()
                .withValuesConvertedBy(new TeamConverter(module.getScoreboard()));

        playersSpec = parser
                .nonOptions("List of player names to remove from the specified team")
                .withValuesConvertedBy(new OfflinePlayerConverter());

        removeAllSpec = parser
                .acceptsAll(ImmutableList.of("a", "all"), "Remove all players from the team");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Team team = teamSpec.value(options);

        Set<OfflinePlayer> players;
        if (options.has(removeAllSpec)) {
            players = team.getPlayers();
        } else {
            players = Sets.intersection(team.getPlayers(), Sets.newHashSet(playersSpec.values(options)));
        }

        for (OfflinePlayer player : players) {
            team.removePlayer(player);
        }

        Set<OfflinePlayer> finalTeam = team.getPlayers();
        String members = finalTeam.size() == 0 ? ChatColor.DARK_GRAY + "No members" : Joiner.on(", ").join(Iterables.transform(team.getPlayers(), FunctionalUtil.PLAYER_NAME_FETCHER));

        sender.sendMessage(String.format(COMPLETE, players.size(), members));
        return true;
    }
}
