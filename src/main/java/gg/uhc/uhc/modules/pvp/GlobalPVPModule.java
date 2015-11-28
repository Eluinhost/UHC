/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.pvp.GlobalPVPModule
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

package gg.uhc.uhc.modules.pvp;

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

public class GlobalPVPModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "PVP";

    protected WorldMatcher worlds;

    public GlobalPVPModule() {
        setId("PVP");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.IRON_SWORD);
        this.icon.setWeight(ModuleRegistry.CATEGORY_WORLD);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        worlds = new WorldMatcher(config, ImmutableList.of("world to not touch on enable/disable"), false);

        super.initialize();
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            if (worlds.worldMatches(world)) {
                world.setPVP(true);
            }
        }
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            if (worlds.worldMatches(world)) {
                world.setPVP(false);
            }
        }
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        World world = event.getWorld();

        if (worlds.worldMatches(world)) {
            world.setPVP(isEnabled());
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
