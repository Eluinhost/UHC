/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.xp.NerfQuartzXPModule
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

package gg.uhc.uhc.modules.xp;

import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Random;

public class NerfQuartzXPModule extends DisableableModule implements Listener {

    protected static final Random RANDOM = new Random();
    protected static final String DROP_COUNT_LOWER_KEY = "drop count min";
    protected static final String DROP_COUNT_HIGHER_KEY = "drop count max";
    protected static final String ICON_NAME = "Nerfed Quartz XP";

    protected static final int REGULAR_RATE_LOWER_RANGE = 2;
    protected static final int REGULAR_RATE_HIGHER_RANGE = 5;
    protected static final int DEFAULT_LOWER_RANGE = 1;
    protected static final int DEFAULT_HIGHER_RANGE = 2;

    protected int lower;
    protected int higher;

    public NerfQuartzXPModule() {
        setId("NerfQuartzXP");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.QUARTZ);
        this.icon.setWeight(ModuleRegistry.CATEGORY_MISC);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains(DROP_COUNT_LOWER_KEY)) {
            config.set(DROP_COUNT_LOWER_KEY, DEFAULT_LOWER_RANGE);
        }

        if (!config.contains(DROP_COUNT_HIGHER_KEY)) {
            config.set(DROP_COUNT_HIGHER_KEY, DEFAULT_HIGHER_RANGE);
        }

        lower = config.getInt(DROP_COUNT_LOWER_KEY);
        higher = config.getInt(DROP_COUNT_HIGHER_KEY);

        Preconditions.checkArgument(lower >= 0, "Min value must be >= 0");
        Preconditions.checkArgument(higher >= 0, "Max value must be >= 0");
        Preconditions.checkArgument(higher >= lower, "Max but be >= min");

        super.initialize();
    }

    @Override
    protected List<String> getEnabledLore() {
        return messages.evalTemplates("lore", ImmutableMap.of("lower", lower, "higher", higher));
    }

    @Override
    protected List<String> getDisabledLore() {
        return messages.evalTemplates(
                "lore",
                ImmutableMap.of(
                        "lower", REGULAR_RATE_LOWER_RANGE,
                        "higher", REGULAR_RATE_HIGHER_RANGE
                )
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        if (!isEnabled() || event.getBlock().getType() != Material.QUARTZ_ORE) return;

        final int count;
        if (higher == lower) {
            count = higher;
        } else {
            count = RANDOM.nextInt(higher - lower + 1) + lower;
        }

        event.setExpToDrop(count);
    }


    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
