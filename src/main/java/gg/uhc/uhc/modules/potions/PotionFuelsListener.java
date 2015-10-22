/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.potions.PotionFuelsListener
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

package gg.uhc.uhc.modules.potions;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class PotionFuelsListener implements Listener {

    protected static final String NOT_ALLOWED = ChatColor.RED + "The material `%s` is not allowed to be brewed with.";

    protected final Map<Material, String> messages = Maps.newHashMap();
    protected final Set<Material> disabled = messages.keySet();

    protected void addMaterial(Material material, String message) {
        messages.put(material, message);
    }

    protected void removeMaterial(Material material) {
        disabled.remove(material);
    }

    // cancel hoppers moving the item into the stand
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (event.getDestination().getType() != InventoryType.BREWING) return;

        if (disabled.contains(event.getItem().getType())) {
            event.setCancelled(true);
        }
    }

    // stop dragging over the fuel slot
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;

        // if it's not a disabled type do nothing
        if (!disabled.contains(event.getOldCursor().getType())) return;

        // check if they dragged over the fuel
        // 3 is the fuel slot
        if (event.getRawSlots().contains(3)) {
            event.getWhoClicked().sendMessage(messages.get(event.getOldCursor().getType()));
            event.setCancelled(true);
        }
    }

    // cancel click events going into the stand
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;

        // quick exit
        if (disabled.size() == 0) return;

        // clicked outside of the window
        if (event.getClickedInventory() == null) return;

        InventoryType clicked = event.getClickedInventory().getType();

        // get any relevant stack to check the type of based on the action took
        Optional<ItemStack> relevant = Optional.absent();
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                // only worry about player -> stand
                if (clicked == InventoryType.PLAYER) {
                    relevant = Optional.fromNullable(event.getCurrentItem());
                }
                break;
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
                // only worry about within a stand
                if (clicked == InventoryType.BREWING) {
                    relevant = Optional.fromNullable(event.getCursor());
                }
                break;
            case HOTBAR_SWAP:
                // only worry about within a stand
                if (clicked == InventoryType.BREWING) {
                    relevant = Optional.fromNullable(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
                }
                break;
        }

        if (relevant.isPresent() && disabled.contains(relevant.get().getType())) {
            event.getWhoClicked().sendMessage(messages.get(relevant.get().getType()));
            event.setCancelled(true);
        }
    }
}
