package gg.uhc.uhc.modules.team;

import com.google.common.collect.ImmutableList;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

public class ClearTeamsCommand extends OptionCommand {

    protected static final String COMPLETE = ChatColor.AQUA + "Cleared %d teams of %d players total";

    protected final TeamModule module;

    protected final OptionSpec<Void> allSpec;

    public ClearTeamsCommand(TeamModule module) {
        this.module = module;

        allSpec = parser
                .acceptsAll(ImmutableList.of("a", "all"), "Clears all teams, not just UHC ones");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<Team> teams = options.has(allSpec) ? module.getScoreboard().getTeams() : module.getTeams().values();

        int count = 0;
        for (Team team : teams) {
            for (OfflinePlayer player : team.getPlayers()) {
                team.removePlayer(player);
                count++;
            }
        }

        sender.sendMessage(String.format(COMPLETE, teams.size(), count));
        return true;
    }
}
