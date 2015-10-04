package gg.uhc.uhc.command;

import gg.uhc.uhc.inventory.IconInventory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowIconsCommand implements CommandExecutor {

    protected static final String PLAYERS_ONLY = ChatColor.RED + "This command is intended for players only.";

    protected final IconInventory inventory;

    public ShowIconsCommand(IconInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PLAYERS_ONLY);
            return true;
        }

        inventory.showTo((Player) sender);
        return true;
    }
}
