/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.health.ChatHealthPrependModule
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

import com.google.common.collect.ImmutableSortedMap;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.NavigableMap;

public class ChatHealthPrependModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Health before chat";

    protected final NavigableMap<Double, String> PREFIXES = ImmutableSortedMap
            .<Double, String>naturalOrder()
            .put(0D,                               ChatColor.DARK_RED + "❘❘❘❘❘❘❘❘❘❘")
            .put(10D,  ChatColor.DARK_GREEN + "❘" + ChatColor.DARK_RED + "❘❘❘❘❘❘❘❘❘")
            .put(20D,  ChatColor.DARK_GREEN + "❘❘" + ChatColor.DARK_RED + "❘❘❘❘❘❘❘❘")
            .put(30D,  ChatColor.DARK_GREEN + "❘❘❘" + ChatColor.DARK_RED + "❘❘❘❘❘❘❘")
            .put(40D,  ChatColor.DARK_GREEN + "❘❘❘❘" + ChatColor.DARK_RED + "❘❘❘❘❘❘")
            .put(50D,  ChatColor.DARK_GREEN + "❘❘❘❘❘" + ChatColor.DARK_RED + "❘❘❘❘❘")
            .put(60D,  ChatColor.DARK_GREEN + "❘❘❘❘❘❘" + ChatColor.DARK_RED + "❘❘❘❘")
            .put(70D,  ChatColor.DARK_GREEN + "❘❘❘❘❘❘❘" + ChatColor.DARK_RED + "❘❘❘")
            .put(80D,  ChatColor.DARK_GREEN + "❘❘❘❘❘❘❘❘" + ChatColor.DARK_RED + "❘❘")
            .put(90D,  ChatColor.DARK_GREEN + "❘❘❘❘❘❘❘❘❘" + ChatColor.DARK_RED + "❘")
            .put(100D, ChatColor.DARK_GREEN + "❘❘❘❘❘❘❘❘❘❘")
            .build();

    public ChatHealthPrependModule() {
        this.iconName = ICON_NAME;

        this.icon.setType(Material.NAME_TAG);
    }

    @Override
    public void rerender() {
        super.rerender();

        icon.setLore(isEnabled() ? "Health is shown before chat messages" : "Chat messages are not modified");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent event) {
        if (!isEnabled()) return;

        double percent = event.getPlayer().getHealth() / event.getPlayer().getMaxHealth() * 100D;

        event.setFormat(PREFIXES.ceilingEntry(percent).getValue() + ChatColor.RESET + " " + event.getFormat());
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }
}
