package gg.uhc.uhc.modules.team.requests;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RequestListCommand implements CommandExecutor {

    protected static final String LINE_FORMAT = ChatColor.DARK_GRAY + "ID %d from '%s' to team with: " + ChatColor.DARK_PURPLE + "%s ";
    protected static final String NO_REQUESTS = ChatColor.AQUA + "There are curently no team requests waiting";

    protected final RequestManager requestManager;

    public RequestListCommand(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<TeamRequest> requests = requestManager.getRequests();

        if (requests.size() == 0) {
            sender.sendMessage(NO_REQUESTS);
        } else {
            for (TeamRequest request : requests) {
                sender.sendMessage(String.format(LINE_FORMAT, request.getId(), request.getOwnerName(), Joiner.on(", ").join(request.getOthers())));
            }
        }

        return true;
    }
}
