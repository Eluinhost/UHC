/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.portals.EndModule
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

package gg.uhc.uhc.modules.portals;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.team.FunctionalUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.Set;

public class EndModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "The End";

    public EndModule() {
        setId("TheEnd");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.ENDER_PORTAL_FRAME);
        this.icon.setWeight(ModuleRegistry.CATEGORY_WORLD);
    }

    @Override
    public void onDisable() {
        Set<OfflinePlayer> players = Sets.newHashSet();
        Set<String> worlds = Sets.newHashSet();

        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.THE_END) {
                worlds.add(world.getName());
                players.addAll(world.getPlayers());
            }
        }

        if (players.size() == 0) return;

        Joiner joiner = Joiner.on(", ");
        String playerNames = joiner.join(Iterables.transform(players, FunctionalUtil.PLAYER_NAME_FETCHER));
        String worldNames = joiner.join(worlds);

        String message = messages.evalTemplate("notification", ImmutableMap.of("players", playerNames, "worlds", worldNames));
        Bukkit.getConsoleSender().sendMessage(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("uhc.broadcast.enddisable")) {
                player.sendMessage(message);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityPortalEvent event) {
        if (isEnabled()) return;

        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END)
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerPortalEvent event) {
        if (isEnabled()) return;

        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(messages.getRaw("disabled message"));
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
