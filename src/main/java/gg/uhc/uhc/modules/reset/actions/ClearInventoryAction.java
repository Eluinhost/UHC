/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.reset.actions.ClearInventoryAction
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

package gg.uhc.uhc.modules.reset.actions;

import com.google.common.base.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class ClearInventoryAction extends Action {

    protected ItemStack[] contents;
    protected ItemStack[] armourContents;
    protected ItemStack onCursor;
    protected Optional<ItemStack[]> crafting;


    public ClearInventoryAction(UUID uuid) {
        super(uuid);
    }

    @Override
    protected void run(Player player) {
        PlayerInventory inv = player.getInventory();

        // clear main inventory
        contents = inv.getContents();
        inv.clear();

        // clear armour slots
        armourContents = inv.getArmorContents();
        inv.setArmorContents(null);

        // clear if they have something on their cursour currently
        onCursor = player.getItemOnCursor();
        player.setItemOnCursor(new ItemStack(Material.AIR));

        // if they have a crafting inventory open clear items from it too
        // stops storing items in crafting slots bypassing clear inventories
        InventoryView openInventory = player.getOpenInventory();
        if(openInventory.getType() == InventoryType.CRAFTING) {
            crafting = Optional.of(openInventory.getTopInventory().getContents());
            openInventory.getTopInventory().clear();
        } else {
            crafting = Optional.absent();
        }
    }

    @Override
    protected void revert(Player player) {
        PlayerInventory inv = player.getInventory();

        inv.setContents(contents);
        inv.setArmorContents(armourContents);
        player.setItemOnCursor(onCursor);

        if (crafting.isPresent()) {
            // add back to main inventory instead of the crafting slots
            inv.addItem(crafting.get());
        }
    }
}
