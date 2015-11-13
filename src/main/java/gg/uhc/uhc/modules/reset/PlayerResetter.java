/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.reset.PlayerResetter
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.modules.reset;

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
        inv.setArmorContents(null);

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
