/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.potions.BrewingRestricter
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public class BrewingRestricter implements Listener {

    public static final Set<Integer> INGREDIENT_SLOTS = ImmutableSet.of(3);
    public static final Set<Integer> OUTPUT_SLOTS = ImmutableSet.of(0, 1, 2);

    protected final static Predicate<ItemStack> IS_NULL_OR_EMPTY = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack itemStack) {
            return itemStack == null || itemStack.getType() == Material.AIR;
        }
    };

    public boolean isIngredientItemAllowed(ItemStack ingredient, Collection<ItemStack> potions) {
        if (potions.size() == 0) return true;

        // TODO
        return false;
    }

    public boolean isOutputItemAllowed(ItemStack output, ItemStack ingredient) {
        if (IS_NULL_OR_EMPTY.apply(ingredient)) return true;

        // TODO
        return false;
    }

    protected Collection<ItemStack> getOutputSlots(final BrewerInventory inventory) {
        // TODO check slot ids
        return Collections2.filter(
            Collections2.transform(
                OUTPUT_SLOTS,
                new Function<Integer, ItemStack>() {
                    @Override
                    public ItemStack apply(Integer slotId) {
                        return inventory.getItem(slotId);
                    }
                }
            ),
            Predicates.not(IS_NULL_OR_EMPTY)
        );
    }

    // cancel hoppers moving disallowed items into the stand
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (event.getDestination().getType() != InventoryType.BREWING) return;

        // TODO check hopper side so we can determine the difference between fuel and ingredient blaze powder
        boolean isIngredient = true;
        boolean isOutput = true;

        BrewerInventory inv = (BrewerInventory) event.getDestination();

        if (isIngredient && !isIngredientItemAllowed(event.getItem(), getOutputSlots(inv))) {
            event.setCancelled(true);
            return;
        }

        if (isOutput && !isOutputItemAllowed(event.getItem(), inv.getIngredient())) {
            event.setCancelled(true);
            return;
        }
    }

    // stop dragging over slots
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;

        BrewerInventory inv = (BrewerInventory) event.getInventory();
        ItemStack cursorStack = event.getOldCursor();
        Set<Integer> affectedSlots = Sets.newHashSet(event.getRawSlots());

        if (Sets.intersection(INGREDIENT_SLOTS, affectedSlots).size() > 0 && !isIngredientItemAllowed(cursorStack, getOutputSlots(inv))) {
            event.setCancelled(true);
            return;
        }

        if (Sets.intersection(OUTPUT_SLOTS, affectedSlots).size() > 0 && !isOutputItemAllowed(cursorStack, inv.getIngredient())) {
            event.setCancelled(true);
            return;
        }
    }

    // cancel click events going into the stand
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) return;

        // clicked outside of the window
        if (event.getClickedInventory() == null) return;

        InventoryType clicked = event.getClickedInventory().getType();

        // get any relevant stack to check the type of based on the action took
        ItemStack relevant = null;
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                // only worry about player -> stand
                if (clicked == InventoryType.PLAYER) {
                    relevant = event.getCurrentItem();
                }
                break;
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
                // only worry about within a stand
                if (clicked == InventoryType.BREWING) {
                    relevant = event.getCursor();
                }
                break;
            case HOTBAR_SWAP:
                // only worry about within a stand
                if (clicked == InventoryType.BREWING) {
                    relevant = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                }
                break;
        }

        // TODO check item
    }
}
