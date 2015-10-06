package gg.uhc.uhc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PlayerResetter {

    public void reset(Player player) {
        resetHealth(player);
        resetFood(player);
        resetExp(player);
        resetInventory(player);
        resetEffects(player);
    }

    public void resetEffects(Player player) {
        Collection<PotionEffect> effects = player.getActivePotionEffects();

        for (PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
        }
    }

    public void resetHealth(Player player) {
        player.setHealth(player.getMaxHealth());
    }

    public void resetFood(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(5.0F);
        player.setExhaustion(0F);
    }

    public void resetExp(Player player) {
        player.setExp(0F);
        player.setLevel(0);
        player.setTotalExperience(0);
    }

    public void resetInventory(Player player) {
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
    }
}
