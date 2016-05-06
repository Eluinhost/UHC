/*
 * Project: UHC
 * Class: gg.uhc.uhc.inventory.IconInventory
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

package gg.uhc.uhc.inventory;

import gg.uhc.uhc.UHCPluginDisableEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class IconInventory implements IconUpdateListener, Listener {

    protected static final int INVENTORY_WIDTH = 9;

    protected final List<IconStack> icons = Lists.newArrayList();
    protected final Set<Inventory> openedInventories = Sets.newHashSet();

    protected final String title;

    public IconInventory(String title) {
        this.title = title;
    }

    public void showTo(HumanEntity entity) {
        // how many icons do we want to show
        final int iconCount = icons.size();

        // how many slots are required, in increments of 9, min 9, max 54
        final int slotsRequired = Math.max(
                9,
                Math.min(
                        54,
                        INVENTORY_WIDTH * ((iconCount + INVENTORY_WIDTH - 1) / INVENTORY_WIDTH)
                )
        );

        // Create and render inventory
        final Inventory inventory = Bukkit.createInventory(null, slotsRequired, title);
        final ItemStack[] contents = new ItemStack[slotsRequired];
        for (int i = 0; i < icons.size(); i++) {
            contents[i] = icons.get(i);
        }

        inventory.setContents(contents);

        // Show to player
        entity.openInventory(inventory);
        openedInventories.add(inventory);
    }

    public void registerNewIcon(IconStack icon) {
        icon.registerUpdateHandler(this);
        add(icon);
    }

    protected int add(IconStack icon) {
        // check where to insert and
        // add item to list in the correct location
        final int index = indexToInsert(icon);
        icons.add(index, icon);
        reopenForCurrentViewers();

        return index;
    }

    protected int indexToInsert(IconStack icon) {
        int insertionIndex = Collections.binarySearch(icons, icon);

        // weight wasn't found so it returns (-(insertion index) -1)
        if (insertionIndex < 0) {
            insertionIndex = -(insertionIndex + 1);
        }

        return insertionIndex;
    }

    protected Collection<HumanEntity> getCurrentViewers() {
        final List<HumanEntity> viewers = Lists.newArrayList();
        for (final Inventory openedInventory : openedInventories) {
            viewers.addAll(openedInventory.getViewers());
        }

        return viewers;
    }

    protected void reopenForCurrentViewers() {
        for (final HumanEntity viewer : getCurrentViewers()) {
            viewer.closeInventory();
            showTo(viewer);
        }
    }

    @Override
    public void onUpdate(IconStack icon) {
        final int index = icons.indexOf(icon);

        // this isn't one of our icons
        if (index < 0) return;

        // refresh the item
        for (final Inventory openedInventory : openedInventories) {
            openedInventory.setItem(index, icon);
        }
    }

    @Override
    public void onWeightUpdate(IconStack icon) {
        final int index = icons.indexOf(icon);

        // this isn't one of our icons
        if (index < 0) return;

        // remove from the list
        icons.remove(index);

        reopenForCurrentViewers();

        // readd to the list
        final int newIndex = add(icon);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();
        if (!inventory.getTitle().equals(title)) return;

        event.setCancelled(true);

        final int slot = event.getRawSlot();

        if (slot < 0 || slot >= inventory.getSize() || slot >= icons.size()) return;

        // call event
        final IconStack icon = icons.get(slot);

        if (icon != null) {
            icon.onClick((Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        openedInventories.remove(event.getInventory());
    }

    @EventHandler
    public void on(UHCPluginDisableEvent event) {
        for (final HumanEntity viewer : getCurrentViewers()) {
            viewer.closeInventory();
        }

        openedInventories.clear();
    }
}
