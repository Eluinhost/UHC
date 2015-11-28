/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.health.HealthRegenerationModule
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

package gg.uhc.uhc.modules.health;

import com.google.common.collect.ImmutableList;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.WorldMatcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class HealthRegenerationModule extends DisableableModule implements Listener {

    protected static final String GAME_RULE = "naturalRegeneration";
    protected static final String ICON_NAME = "Health Regeneration";

    protected static final short ENABLED_DATA = new Potion(PotionType.REGEN).toDamageValue();
    protected static final short DISABLED_DATA = new Potion(PotionType.WATER).toDamageValue();

    protected WorldMatcher worlds;

    public HealthRegenerationModule() {
        setId("HealthRegen");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.POTION);
        this.icon.setWeight(ModuleRegistry.CATEGORY_HEALTH);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        worlds = new WorldMatcher(config, ImmutableList.of("world to not touch on enable/disable"), false);

        super.initialize();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setDurability(ENABLED_DATA);
            icon.setLore(messages.getRaw("enabled lore"));
        } else {
            icon.setDurability(DISABLED_DATA);
            icon.setLore(messages.getRaw("disabled lore"));
        }
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            if (worlds.worldMatches(world)) {
                world.setGameRuleValue(GAME_RULE, "true");
            }
        }
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            if (worlds.worldMatches(world)) {
                world.setGameRuleValue(GAME_RULE, "false");
            }
        }
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        event.getWorld().setGameRuleValue(GAME_RULE, isEnabled() ? "true" : "false");
    }
}
