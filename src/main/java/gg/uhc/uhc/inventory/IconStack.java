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

    protected int getWeight() {
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
