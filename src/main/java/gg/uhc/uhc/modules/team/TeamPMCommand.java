package gg.uhc.uhc.modules.team;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TeamPMCommand implements CommandExecutor {

    protected static final String NOT_IN_TEAM = ChatColor.RED + "You are not in a team";
    protected static final String PLAYER_ONLY = ChatColor.RED + "This command can only be used by a player";
    protected static final String FORMAT = "%s[%s]" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " %s: " + ChatColor.DARK_PURPLE + "%s";
    protected static final String NO_MESSAGE = ChatColor.RED + "You must provide a message to send";

    protected final TeamModule module;

    public TeamPMCommand(TeamModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PLAYER_ONLY);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(NO_MESSAGE);
            return true;
        }

        Team team = module.getScoreboard().getPlayerTeam((OfflinePlayer) sender);

        if (team == null) {
            sender.sendMessage(NOT_IN_TEAM);
            return true;
        }

        Iterable<Player> online = Iterables.filter(Iterables.transform(team.getPlayers(), FunctionalUtil.ONLINE_VERSION), Predicates.notNull());

        String message = String.format(FORMAT, team.getPrefix(), team.getDisplayName(), sender.getName(), Joiner.on(" ").join(args));
        for (Player player : online) {
            player.sendMessage(message);
        }
        return true;
    }
}
