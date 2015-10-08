package gg.uhc.uhc.modules.team;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoTeamCommand implements CommandExecutor {

    protected static final String FORMAT = ChatColor.AQUA + "Players without a team: " + ChatColor.DARK_PURPLE + "%s";

    protected final TeamModule module;

    public NoTeamCommand(TeamModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Iterable<? extends Player> noTeam = Iterables.filter(Bukkit.getOnlinePlayers(), Predicates.not(HAS_TEAM));

        String noTeamNames = Joiner.on(", ").join(Iterables.transform(noTeam, FunctionalUtil.PLAYER_NAME_FETCHER));

        if (noTeamNames.length() == 0) noTeamNames = ChatColor.DARK_GRAY + "No players found";

        sender.sendMessage(String.format(FORMAT, noTeamNames));
        return true;
    }

    protected final Predicate<Player> HAS_TEAM = new Predicate<Player>() {
        @Override
        public boolean apply(Player input) {
            return module.getScoreboard().getPlayerTeam(input) != null;
        }
    };
}
