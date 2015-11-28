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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.NumberFormat;
import java.util.List;
import java.util.NavigableMap;

public class ChatHealthPrependModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Health before chat";
    protected static final String TYPE_KEY = "use numbers";
    protected static final NumberFormat FORMATTER = NumberFormat.getInstance();

    static {
        FORMATTER.setMaximumFractionDigits(1);
        FORMATTER.setMinimumFractionDigits(0);
    }

    protected final NavigableMap<Double, String> BARS = ImmutableSortedMap
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

    protected final NavigableMap<Double, String> PERCENTAGE_COLOURS = ImmutableSortedMap
            .<Double, String>naturalOrder()
            .put(0D, ChatColor.GRAY.toString())
            .put(33D, ChatColor.RED.toString())
            .put(66D, ChatColor.YELLOW.toString())
            .put(100D, ChatColor.GREEN.toString())
            .build();

    protected boolean useNumbers;

    public ChatHealthPrependModule() {
        setId("ChatHealth");

        this.iconName = ICON_NAME;

        this.icon.setType(Material.NAME_TAG);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains(TYPE_KEY)) {
            config.set(TYPE_KEY, false);
        }

        useNumbers = config.getBoolean(TYPE_KEY);

        super.initialize();
    }

    @Override
    protected List<String> getEnabledLore() {
        return messages.evalTemplates(ENABLED_LORE_PATH, ImmutableMap.of("type", useNumbers ? "percentage numbers" : "bars"));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent event) {
        if (!isEnabled()) return;

        double percent = event.getPlayer().getHealth() / event.getPlayer().getMaxHealth() * 100D;

        StringBuilder format = new StringBuilder();

        if (useNumbers) {
            format.append(PERCENTAGE_COLOURS.ceilingEntry(percent).getValue())
                    .append(FORMATTER.format(percent))
                    .append("%%"); // double percent because it is a string.format string
        } else {
            format.append(BARS.ceilingEntry(percent).getValue());
        }

        format.append(ChatColor.RESET)
                .append(" ")
                .append(event.getFormat());

        event.setFormat(format.toString());
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }
}
