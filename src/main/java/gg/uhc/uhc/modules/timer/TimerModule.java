/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.timer.TimerModule
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

package gg.uhc.uhc.modules.timer;

import gg.uhc.flagcommands.converters.EnumConverter;
import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.timer.messages.TimerMessage;
import gg.uhc.uhc.modules.timer.renderer.*;
import gg.uhc.uhc.util.ActionBarMessenger;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;

public class TimerModule extends Module {

    protected static final String USE_BOSS_BAR_KEY = "use boss bar";
    protected static final String BOSS_BAR_COLOUR_KEY = "boss bar colour";
    protected static final String BOSS_BAR_STYLE_KEY = "boss bar style";
    protected static final String USE_ACTION_BAR_KEY = "use action bar";
    protected static final String USE_TAB_LIST_KEY = "use tab list";
    protected static final String TAB_LIST_POSITION_KEY = "tab list position";

    protected static final int TICKS_PER_SECOND = 20;
    protected static final double PERCENT_MULTIPLIER = 100D;

    protected TimerRenderer renderer;
    protected BukkitTask currentTask;
    protected TimerMessage currentMessage;
    protected long currentTick;
    protected long targetTick;

    public TimerModule() {
        setId("Timer");

        this.icon.setDisplayName(ChatColor.GREEN + "Actionbar Timer");
        this.icon.setType(Material.WATCH);
        this.icon.setWeight(ModuleRegistry.CATEGORY_MISC);
    }

    protected TimerRenderer setupBossBar(ConfigurationSection section) {
        if (!config.contains(USE_BOSS_BAR_KEY)) {
            config.set(USE_BOSS_BAR_KEY, true);
        }

        if (!config.getBoolean(USE_BOSS_BAR_KEY)) {
            return null;
        }

        try {
            if (!config.contains(BOSS_BAR_COLOUR_KEY)) {
                config.set(BOSS_BAR_COLOUR_KEY, BarColor.BLUE.name());
            }
            if (!config.contains(BOSS_BAR_STYLE_KEY)) {
                config.set(BOSS_BAR_STYLE_KEY, BarStyle.SOLID.name());
            }

            // attempt to parse the colour
            BarColor colour;
            try {
                colour = EnumConverter.forEnum(BarColor.class).convert(config.getString(BOSS_BAR_COLOUR_KEY));
            } catch (ValueConversionException ex) {
                plugin.getLogger().warning("Invalid colour for boss bar, switching to blue");
                config.set(BOSS_BAR_COLOUR_KEY, BarColor.BLUE.name());
                colour = BarColor.BLUE;
            }

            // attempt to parse the style
            BarStyle style;
            try {
                style = EnumConverter.forEnum(BarStyle.class).convert(config.getString(BOSS_BAR_STYLE_KEY));
            } catch (ValueConversionException ex) {
                plugin.getLogger().warning("Invalid style for boss bar, switching to solid");
                config.set(BOSS_BAR_STYLE_KEY, BarStyle.SOLID.name());
                style = BarStyle.SOLID;
            }

            // setup the renderer
            return new BossBarRenderer(Bukkit.createBossBar("", colour, style));
        } catch (NoClassDefFoundError ex) {
            // happens when boss bar API not implemented, < 1.9
            plugin.getLogger().severe(
                    "Could not load the boss bar timer type, this is only supported in 1.9+, "
                            + "disabling in the config file..."
            );

            // turn of the boss bar in the config to stop it happening over again
            config.set(USE_BOSS_BAR_KEY, false);
            return null;
        }
    }

    protected TimerRenderer setupActionBar(ConfigurationSection section) {
        if (!config.contains(USE_ACTION_BAR_KEY)) {
            config.set(USE_ACTION_BAR_KEY, true);
        }


        if (!config.getBoolean(USE_ACTION_BAR_KEY)) {
            return null;
        }


        try {
            return new ActionBarRenderer(new ActionBarMessenger(ProtocolLibrary.getProtocolManager()));
        } catch (NoClassDefFoundError ex) {
            // Happens when protocollib isn't installed, don't disable in config just give a warning
            plugin.getLogger().severe(
                    "Could not load the action bar timer type,"
                            + "this is only supported when ProtocolLib is installed."
            );
            return null;
        }
    }

    protected TimerRenderer setupTabList(ConfigurationSection section) {
        if (!config.contains(USE_TAB_LIST_KEY)) {
            config.set(USE_TAB_LIST_KEY, true);
        }

        if (!config.getBoolean(USE_TAB_LIST_KEY)) {
            return null;
        }

        if (!config.contains(TAB_LIST_POSITION_KEY)) {
            config.set(TAB_LIST_POSITION_KEY, TabListPosition.BOTTOM.name());
        }

        // attempt to parse the style
        TabListPosition position;
        try {
            position = EnumConverter.forEnum(TabListPosition.class).convert(config.getString(TAB_LIST_POSITION_KEY));
        } catch (ValueConversionException ex) {
            plugin.getLogger().warning("Invalid postion for tab list, switching to BOTTOM");
            config.set(TAB_LIST_POSITION_KEY, TabListPosition.BOTTOM.name());
            position = TabListPosition.BOTTOM;
        }

        try {
            return new TabListRenderer(plugin, ProtocolLibrary.getProtocolManager(), position);
        } catch (NoClassDefFoundError ex) {
            // Happens when protocollib isn't installed, don't disable in config just give a warning
            plugin.getLogger().severe(
                    "Could not load the tab list timer type, this is only supported when ProtocolLib is installed."
            );
            return null;
        }
    }


    @Override
    public void initialize() throws InvalidConfigurationException {
        this.icon.setLore(messages.getRawStrings("lore"));

        final Iterator<TimerRenderer> renderers = Iterables.filter(
                Lists.newArrayList(
                        setupBossBar(config),
                        setupTabList(config),
                        setupActionBar(config)
                ),
                Predicates.notNull()
        ).iterator();

        // No renders worked, throw an error to stop the module from loading
        if (!renderers.hasNext()) {
            throw new InvalidConfigurationException(
                    "The timer module can only be used when the Boss bar, Action bar or Tab list type are loaded"
            );
        }

        // if more than one renderer is chosen pick the first one
        renderer = renderers.next();

        if (renderers.hasNext()) {
            plugin.getLogger().warning(
                    "More than one style of timer is being used, using only the first one loaded "
                    + "(" + renderer.getClass().getName() + ")"
            );
        }
    }

    public void startTimer(TimerMessage message, long seconds) {
        Preconditions.checkArgument(seconds > 0, "Timers must be longer than 0 seconds");
        Preconditions.checkNotNull(message, "Message cannot be null");

        if (currentTask != null) {
            cancel();
        }

        // start at tick 0
        currentTick = 0;
        targetTick = seconds;
        currentMessage = message;

        // start timer for 1 second loop
        currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                currentTick++;

                if (currentTick == targetTick) {
                    TimerModule.this.cancel();
                } else {
                    updateMessage();
                }
            }
        }.runTaskTimer(plugin, TICKS_PER_SECOND, TICKS_PER_SECOND);

        renderer.onStart(message.getMessage(targetTick));
    }

    public void extend(long ticks) {
        Preconditions.checkState(currentTask != null, "There is no timer in progress");
        Preconditions.checkArgument(ticks > 0, "Must provide a positive value to extend the timer");

        targetTick += ticks;
    }

    protected String compileMessage() {
        return currentMessage.getMessage(targetTick - currentTick);
    }

    public void updateMessage() {
        Preconditions.checkState(currentTask != null, "There is no timer in progress");

        final double percentRemaining = 100D - (((double) currentTick / (double) targetTick) * PERCENT_MULTIPLIER);

        renderer.onUpdate(compileMessage(), percentRemaining / PERCENT_MULTIPLIER);
    }

    public void cancel() {
        if (currentTask == null) return;

        currentTask.cancel();
        currentTask = null;
        renderer.onStop();
    }

    public boolean isRunning() {
        return currentTask != null;
    }
}
