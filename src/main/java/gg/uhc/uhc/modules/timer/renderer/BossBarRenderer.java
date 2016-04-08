/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.timer.renderer.BossBarRenderer
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

package gg.uhc.uhc.modules.timer.renderer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class BossBarRenderer implements TimerRenderer, Listener {
    protected final BossBar bossBar;

    protected boolean isRendering = false;

    public BossBarRenderer(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (!isRendering) return;

        bossBar.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        bossBar.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void on(PluginDisableEvent event) {
        onStop();
    }

    @Override
    public void onStart(String message) {
        // Initial render for full bar
        onUpdate(message, 1D);

        // Add all currently online players otherwise the bar doesn't render
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        isRendering = true;
    }

    @Override
    public void onUpdate(String message, double progress) {
        bossBar.setTitle(message);
        bossBar.setProgress(progress);
    }

    @Override
    public void onStop() {
        bossBar.removeAll();
        isRendering = false;
    }
}