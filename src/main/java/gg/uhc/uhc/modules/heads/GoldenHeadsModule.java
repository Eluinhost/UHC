/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.heads.GoldenHeadsModule
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

package gg.uhc.uhc.modules.heads;

import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.NumberFormat;
import java.util.List;

public class GoldenHeadsModule extends DisableableModule implements Listener {

    public static final String HEAD_NAME = ChatColor.GOLD + "Golden Head";

    protected static final String HEAL_AMOUNT_KEY = "heal amount";
    protected static final String ICON_NAME = "Golden Heads";
    protected static final String LORE_PATH = "lore";
    protected static final NumberFormat FORMATTER = NumberFormat.getNumberInstance();
    protected static final int TICKS_PER_HALF_HEART = 25;
    protected static final int DEFAULT_HEAL_AMOUNT = 6;
    protected static final int CRAFTING_INVENTORY_CENTRE_SLOT_ID = 4;

    static {
        FORMATTER.setMinimumFractionDigits(0);
        FORMATTER.setMaximumFractionDigits(1);
    }

    protected int healAmount;

    public GoldenHeadsModule() {
        setId("GoldenHeads");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.SKULL_ITEM);
        this.icon.setDurability(PlayerHeadProvider.PLAYER_HEAD_DATA);
        this.icon.setWeight(ModuleRegistry.CATEGORY_APPLES);

        // register the new recipe
        final ShapedRecipe modified = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1))
                .shape("AAA", "ABA", "AAA")
                .setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.SKULL_ITEM, PlayerHeadProvider.PLAYER_HEAD_DATA);

        Bukkit.addRecipe(modified);
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int amount) {
        this.healAmount = amount;
        config.set(HEAL_AMOUNT_KEY, this.healAmount);
        saveConfig();
        rerender();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains(HEAL_AMOUNT_KEY)) {
            config.set(HEAL_AMOUNT_KEY, DEFAULT_HEAL_AMOUNT);
        }

        if (!config.isInt(HEAL_AMOUNT_KEY)) {
            throw new InvalidConfigurationException(
                    "Invalid value at " + config.getCurrentPath() + ".heal amount (" + config.get(HEAL_AMOUNT_KEY)
            );
        }

        healAmount = config.getInt(HEAL_AMOUNT_KEY);
        super.initialize();
    }

    @Override
    protected void renderEnabled() {
        super.renderEnabled();
        icon.setAmount(healAmount);
    }

    @Override
    protected void renderDisabled() {
        super.renderDisabled();
        icon.setAmount(0);
    }

    @Override
    protected List<String> getEnabledLore() {
        return messages.evalTemplates(ENABLED_LORE_PATH, ImmutableMap.of("amount", FORMATTER.format(healAmount / 2D)));
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() != Material.GOLDEN_APPLE) return;

        final ItemStack centre = event.getInventory().getMatrix()[CRAFTING_INVENTORY_CENTRE_SLOT_ID];

        if (centre == null || centre.getType() != Material.SKULL_ITEM) return;

        if (!isEnabled()) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }

        final SkullMeta meta = (SkullMeta) centre.getItemMeta();
        final ItemStack goldenHeadItem = getGoldenHeadItem(meta.hasOwner() ? meta.getOwner() : "Manually Crafted");
        event.getInventory().setResult(goldenHeadItem);
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (isEnabled() && isGoldenHead(event.getItem())) {
            event.getPlayer().addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    TICKS_PER_HALF_HEART * healAmount,
                    1
            ));
        }
    }

    public boolean isGoldenHead(ItemStack itemStack) {
        if (itemStack.getType() != Material.GOLDEN_APPLE) return false;

        final ItemMeta im = itemStack.getItemMeta();

        return im.hasDisplayName() && im.getDisplayName().equals(HEAD_NAME);
    }

    public ItemStack getGoldenHeadItem(String playerName) {
        final ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE, 1);

        final ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(HEAD_NAME);

        // add lore
        final ImmutableMap<String, String> context = ImmutableMap.of(
                "player", playerName,
                "amount", Integer.toString(getHealAmount())
        );
        final List<String> lore = getMessageTemaplates().evalTemplates(LORE_PATH, context);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
