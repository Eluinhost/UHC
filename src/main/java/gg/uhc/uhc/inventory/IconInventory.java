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

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class IconInventory implements IconUpdateListener, Listener {

    protected final List<IconStack> icons = Lists.newArrayList();

    protected final String title;
    protected Inventory inventory;

    public IconInventory(String title) {
        this.title = title;
        inventory = Bukkit.createInventory(null, 9, title);
    }

    public void showTo(HumanEntity entity) {
        entity.openInventory(inventory);
    }

    public void registerNewIcon(IconStack icon) {
        icon.registerUpdateHandler(this);
        add(icon);
    }

    protected int add(IconStack icon) {
        // check where to insert and
        // add item to list in the correct location
        int index = indexToInsert(icon);
        icons.add(index, icon);

        // make sure this is enough space now
        ensureInventorySize();

        // rerender all items past this one (inclusive) as they have shifted index right 1
        for (int i = index; i < icons.size(); i++) {
            inventory.setItem(i, icons.get(i));
        }

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

    @Override
    public void onUpdate(IconStack icon) {
        int index = icons.indexOf(icon);

        // this isn't one of our icons
        if (index < 0) return;

        // refresh the item
        inventory.setItem(index, icon);
    }

    @Override
    public void onWeightUpdate(IconStack icon) {
        int index = icons.indexOf(icon);

        // this isn't one of our icons
        if (index < 0) return;

        // remove from the list
        icons.remove(index);

        // readd to the list
        int newIndex = add(icon);

        // rerender the changed items (index+ is handled by the add() method)
        for (int i = newIndex; i < index; i++) {
            inventory.setItem(i, icons.get(i));
        }
    }

    protected void ensureInventorySize() {
        // how many icons do we want to show
        int iconCount = icons.size();

        // how many slots are required, in increments of 9, min 9, max 54
        int slotsRequired = Math.max(9, Math.min(54, 9 * ((iconCount + 8) / 9)));

        // if it's already the right size then do nothing
        if (slotsRequired == inventory.getSize()) return;

        // clear the old inventory just in case
        inventory.clear();

        // create a replacement
        Inventory newInventory = Bukkit.createInventory(null, slotsRequired, title);

        // rerender entire inventory
        ItemStack[] contents = new ItemStack[slotsRequired];
        for (int i = 0; i < icons.size(); i++) {
            contents[i] = icons.get(i);
        }

        newInventory.setContents(contents);

        // replace the old inventory
        for (HumanEntity entity : inventory.getViewers()) {
            entity.closeInventory();
            entity.openInventory(newInventory);
        }

        inventory = newInventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(title)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();

        if (slot < 0 || slot > inventory.getSize()) return;

        // call event
        IconStack icon = icons.get(slot);

        if (icon != null) {
            icon.onClick((Player) event.getWhoClicked());
        }
    }
}
