package gg.uhc.uhc.modules.team;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.OfflinePlayerConverter;
import gg.uhc.flagcommands.converters.TeamConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class TeamAddCommand extends OptionCommand {

    protected static final String COMPLETE = ChatColor.AQUA + "Added %d players, team is now: " + ChatColor.DARK_PURPLE + "%s";

    protected final OptionSpec<Team> teamSpec;
    protected final OptionSpec<OfflinePlayer> playersSpec;

    public TeamAddCommand(TeamModule module) {
        teamSpec = parser
                .acceptsAll(ImmutableList.of("t", "team"), "Name of the team to add the players to")
                .withRequiredArg()
                .required()
                .withValuesConvertedBy(new TeamConverter(module.getScoreboard()));

        playersSpec = parser
                .nonOptions("List of player names to add to the specified team")
                .withValuesConvertedBy(new OfflinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Team team = teamSpec.value(options);
        Set<OfflinePlayer> players = Sets.newHashSet(playersSpec.values(options));
        players.removeAll(team.getPlayers());

        for (OfflinePlayer player : players) {
            team.addPlayer(player);
        }

        Set<OfflinePlayer> finalTeam = team.getPlayers();

        String members = finalTeam.size() == 0 ? ChatColor.DARK_GRAY + "No members" : Joiner.on(", ").join(Iterables.transform(team.getPlayers(), FunctionalUtil.PLAYER_NAME_FETCHER));

        sender.sendMessage(String.format(COMPLETE, players.size(), members));
        return false;
    }
}
