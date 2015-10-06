package gg.uhc.uhc.modules.inventory;

import gg.uhc.uhc.command.OptionCommand;
import gg.uhc.uhc.command.converters.OnlinePlayerConverter;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class ClearInventoryCommand extends OptionCommand {

    protected final OptionSpec<Player> playersSpec;

    public ClearInventoryCommand() {
        playersSpec = parser.nonOptions("List of online players to clear inventories on, leave empty for all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> toClear = playersSpec.values(options);

        if (toClear.size() == 0) {
            toClear = Bukkit.getOnlinePlayers();
        }

        for (Player player : toClear) {
            PlayerInventory inv = player.getInventory();

            // clear main inventory
            inv.clear();

            // clear armour slots
            inv.setHelmet(null);
            inv.setChestplate(null);
            inv.setLeggings(null);
            inv.setBoots(null);

            // clear if they have something on their cursour currently
            player.setItemOnCursor(new ItemStack(Material.AIR));

            // if they have a crafting inventory open clear items from it too
            // stops storing items in crafting slots bypassing clear inventories
            InventoryView openInventory = player.getOpenInventory();
            if(openInventory.getType() == InventoryType.CRAFTING) {
                openInventory.getTopInventory().clear();
            }

            player.sendMessage(ChatColor.AQUA + "Your inventory was cleared");
        }

        sender.sendMessage(ChatColor.AQUA + "Player inventories cleared");
        return true;
    }
}
