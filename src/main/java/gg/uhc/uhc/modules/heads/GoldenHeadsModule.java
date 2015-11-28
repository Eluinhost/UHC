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

import com.google.common.collect.ImmutableMap;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.NumberFormat;
import java.util.List;

public class GoldenHeadsModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Golden Heads";
    protected static final NumberFormat formatter = NumberFormat.getNumberInstance();
    protected static final int TICKS_PER_HALF_HEART = 25;

    static {
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(1);
    }

    protected final PlayerHeadProvider playerHeadProvider;

    protected int healAmount;

    public GoldenHeadsModule(PlayerHeadProvider provider) {
        setId("GoldenHeads");

        this.playerHeadProvider = provider;

        this.iconName = ICON_NAME;
        this.icon.setType(Material.SKULL_ITEM);
        this.icon.setDurability((short) 3);
        this.icon.setWeight(ModuleRegistry.CATEGORY_APPLES);

        // register the new recipe
        ShapedRecipe modified = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1))
                .shape("AAA", "ABA", "AAA")
                .setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.SKULL_ITEM, 3);

        Bukkit.addRecipe(modified);
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int amount) {
        this.healAmount = amount;
        config.set("heal amount", this.healAmount);
        saveConfig();
        rerender();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains("heal amount")) {
            config.set("heal amount", 6);
        }

        if (!config.isInt("heal amount"))
            throw new InvalidConfigurationException("Invalid value at " + config.getCurrentPath() + ".heal amount (" + config.get("heal amount"));

        healAmount = config.getInt("heal amount");

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
        return messages.evalTemplates(ENABLED_LORE_PATH, ImmutableMap.of("amount", formatter.format(healAmount / 2D)));
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() != Material.GOLDEN_APPLE) return;

        ItemStack centre = event.getInventory().getMatrix()[4];

        if (centre == null || centre.getType() != Material.SKULL_ITEM) return;

        if (!isEnabled()) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }

        SkullMeta meta = (SkullMeta) centre.getItemMeta();
        event.getInventory().setResult(playerHeadProvider.getGoldenHeadItem(meta.hasOwner() ? meta.getOwner() : "Manually Crafted"));
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if(isEnabled() && playerHeadProvider.isGoldenHead(event.getItem())) {
            event.getPlayer().addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    TICKS_PER_HALF_HEART * healAmount,
                    1
            ));
        }
    }
}
