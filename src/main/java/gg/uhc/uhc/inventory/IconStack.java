/*
 * Project: UHC
 * Class: gg.uhc.uhc.inventory.IconStack
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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.List;

public class IconStack extends ItemStack implements Comparable<IconStack> {

    protected int weight = 0;

    protected List<ClickHandler> clickHandlers = Lists.newArrayList();
    protected List<IconUpdateListener> iconUpdateListeners = Lists.newArrayList();

    public IconStack(Material defaultMat) {
        this(defaultMat, 1);
    }

    public IconStack(Material type, int amount) {
        super(type, amount);
    }

    public IconStack(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    /**
     * Registers a click handler to this icon. When this icon is clicked the handler will be called.
     *
     * @param handler the handler to register
     */
    public IconStack registerClickHandler(ClickHandler handler) {
        Preconditions.checkNotNull(handler);

        this.clickHandlers.add(handler);
        return this;
    }

    public IconStack registerUpdateHandler(IconUpdateListener handler) {
        Preconditions.checkNotNull(handler);

        this.iconUpdateListeners.add(handler);
        return this;
    }

    protected void onClick(Player player) {
        for (ClickHandler handler : clickHandlers) {
            handler.onClick(player);
        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;

        for (IconUpdateListener listener : iconUpdateListeners) {
            listener.onWeightUpdate(this);
        }
    }

    public void refresh() {
        for (IconUpdateListener handler : iconUpdateListeners) {
            handler.onUpdate(this);
        }
    }

    public void setDisplayName(String displayName) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(displayName);
        setItemMeta(meta);
    }

    public void setLore(String... lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(ImmutableList.copyOf(lore));
        setItemMeta(meta);
    }

    public void setLore(List<String> lore) {
        setLore(lore.toArray(new String[lore.size()]));
    }

    @Override
    public int compareTo(IconStack other) {
        return Ordering.natural().compare(weight, other.weight);
    }

    // ---------------------------------------
    // Overrides on item stack methods below -
    // ---------------------------------------

    public void setTypeId(int type) {
        super.setTypeId(type);

        refresh();
    }

    public void setAmount(int amount) {
        super.setAmount(amount);

        refresh();
    }

    public void setData(MaterialData data) {
        super.setData(data);

        refresh();
    }

    public void setDurability(short durability) {
        super.setDurability(durability);

        refresh();
    }

    public void addUnsafeEnchantment(Enchantment ench, int level) {
        super.addUnsafeEnchantment(ench, level);

        refresh();
    }

    public int removeEnchantment(Enchantment ench) {
        int r = super.removeEnchantment(ench);

        refresh();
        return r;
    }

    public boolean setItemMeta(ItemMeta itemMeta) {
        boolean b = super.setItemMeta(itemMeta);

        refresh();
        return b;
    }
}
