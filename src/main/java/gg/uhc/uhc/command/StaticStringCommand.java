package gg.uhc.uhc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaticStringCommand implements CommandExecutor {

    protected final String message;

    public StaticStringCommand(String message) {
        this.message = message;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(message);
        return true;
    }
}
