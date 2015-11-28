/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.food.ExtendedSaturationModule
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

package gg.uhc.uhc.modules.food;

import com.google.common.collect.ImmutableMap;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.List;

public class ExtendedSaturationModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Extended Saturation";

    protected double multiplier;

    public ExtendedSaturationModule() {
        setId("ExtendedSaturation");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.COOKED_BEEF);
        this.icon.setWeight(ModuleRegistry.CATEGORY_MISC);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        config.set("multiplier", multiplier);
        saveConfig();
        rerender();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains("multiplier")) {
            config.set("multiplier", 2.5D);
        }

        if (!config.isDouble("multiplier") && !config.isInt("multiplier"))
            throw new InvalidConfigurationException("Invalid value at " + config.getCurrentPath() + ".multiplier (" + config.get("multiplier") + ")");

        multiplier = config.getDouble("multiplier");

        super.initialize();
    }

    @Override
    protected List<String> getEnabledLore() {
        return messages.evalTemplates(ENABLED_LORE_PATH, ImmutableMap.of("multiplier", multiplier));
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerItemConsumeEvent event) {
        if (isEnabled()) {
            new SaturationMultiplierRunnable(event.getPlayer().getUniqueId(), event.getPlayer().getSaturation(), multiplier - 1D).runTask(plugin);
        }
    }
}
