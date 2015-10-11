package gg.uhc.uhc.command;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Map;

public class SubcommandCommand implements CommandExecutor {

    protected final Map<String, CommandExecutor> subroutes = Maps.newHashMap();

    public void registerSubcommand(String name, CommandExecutor subcommand) {
        Preconditions.checkState(!subroutes.containsKey(name), "Subroute is already registered");

        subroutes.put(name.toLowerCase(), subcommand);
    }

    protected void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "This command requires an argument. Available: [" + Joiner.on(",").join(subroutes.keySet())+ "]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        CommandExecutor subcommand = subroutes.get(args[0]);

        if (subcommand == null) {
            sendUsage(sender);
            return true;
        }

        // cut subroute arg off and run subcommand
        return subcommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
    }
}
