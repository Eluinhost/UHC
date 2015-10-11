package gg.uhc.uhc.command;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SubcommandCommand implements TabExecutor {

    protected final Map<String, CommandExecutor> commandExecutors = Maps.newHashMap();
    protected final Map<String, TabCompleter> tabCompleters = Maps.newHashMap();

    public void registerSubcommand(String name, TabExecutor tabExecutor) {
        this.registerSubcommand(name, tabExecutor, tabExecutor);
    }

    public void registerSubcommand(String name, CommandExecutor executor) {
        this.registerSubcommand(name, executor, null);
    }

    public void registerSubcommand(String name, CommandExecutor commandExecutor, TabCompleter tabCompleter) {
        Preconditions.checkState(!commandExecutors.containsKey(name), "Subroute is already registered");

        commandExecutors.put(name.toLowerCase(), commandExecutor);

        if (tabCompleter != null) {
            tabCompleters.put(name.toLowerCase(), tabCompleter);
        }
    }

    protected void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "This command requires an argument. Available: [" + Joiner.on(",").join(commandExecutors.keySet())+ "]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        CommandExecutor subcommand = commandExecutors.get(args[0]);

        if (subcommand == null) {
            sendUsage(sender);
            return true;
        }

        // cut subroute arg off and run subcommand
        return subcommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> potentials = Lists.newArrayList();
            StringUtil.copyPartialMatches(args[0], commandExecutors.keySet(), potentials);
            return potentials;
        } else {
            TabCompleter subcommand = tabCompleters.get(args[0]);

            if (subcommand == null) {
                return ImmutableList.of();
            }

            return subcommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
        }
    }
}
