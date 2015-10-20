package gg.uhc.uhc.modules.team.requests;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RequestResponseCommand implements CommandExecutor {

    protected static final String USAGE = ChatColor.RED + "USAGE: accept|deny <request id>";
    protected static final String NONE_WITH_ID = ChatColor.RED + "No request found with the ID %d";

    protected final RequestManager requestManager;
    protected final RequestManager.AcceptState state;

    public RequestResponseCommand(RequestManager requestManager, RequestManager.AcceptState state) {
        this.requestManager = requestManager;
        this.state = state;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(RequestManager.ADMIN_PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to accept or deny requests");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(USAGE);
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(USAGE);
            return true;
        }

        if (!requestManager.finalizeRequest(id, state)) {
            sender.sendMessage(String.format(NONE_WITH_ID, id));
        }

        return true;
    }
}
