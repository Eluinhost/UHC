/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.death.DeathBansModule
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

package gg.uhc.uhc.modules.death;

import gg.uhc.flagcommands.converters.EnumConverter;
import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.util.TimeUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DeathBansModule extends DisableableModule implements Listener {

    protected static final int DEFAULT_DELAY_SECONDS = 20;
    protected static final int TICKS_PER_SECOND = 20;

    protected static final String ACTION_KEY = "action";
    protected static final String MESSAGE_KEY = "message";
    protected static final String DELAY_SECONDS_KEY = "delay";
    protected static final String DURATION_KEY = "duration";
    protected static final String WORLD_NAME_KEY = "world name";
    protected static final String SERVER_NAME_KEY = "server name";

    protected static final EnumConverter<BanType> BAN_PARSER = EnumConverter.forEnum(BanType.class);
    protected static final String ICON_NAME = "Death Bans";

    protected final Map<UUID, BukkitRunnable> timers = Maps.newHashMap();

    protected Set<BanType> types;
    protected String message;
    protected int delay;
    protected long duration;
    protected String worldName;
    protected String serverName;

    public DeathBansModule() {
        setId("DeathBans");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.BARRIER);
        this.icon.setWeight(ModuleRegistry.CATEGORY_DEATH);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains(ACTION_KEY)) {
            config.set(ACTION_KEY, "BAN+KICK");
        }

        if (!config.contains(MESSAGE_KEY)) {
            config.set(MESSAGE_KEY, "RIP");
        }
        this.message = config.getString(MESSAGE_KEY);

        if (!config.contains(DELAY_SECONDS_KEY)) {
            config.set(DELAY_SECONDS_KEY, DEFAULT_DELAY_SECONDS);
        }
        this.delay = config.getInt(DELAY_SECONDS_KEY);

        final ImmutableSet.Builder<BanType> chosenTypes = ImmutableSet.builder();
        for (final String banTypeString : config.getString(ACTION_KEY).split("\\+")) {
            try {
                chosenTypes.add(BAN_PARSER.convert(banTypeString));
            } catch (ValueConversionException ex) {
                throw new InvalidConfigurationException("Invalid ban type given", ex);
            }
        }

        this.types = chosenTypes.build();

        // ensure extra fields are set
        for (final BanType type : this.types) {
            switch (type) {
                case BAN:
                    if (!config.contains(DURATION_KEY)) {
                        config.set(DURATION_KEY, "1d");
                    }
                    duration = TimeUtil.getSeconds(config.getString(DURATION_KEY));
                    break;
                case MOVE_WORLD:
                    if (!config.contains(WORLD_NAME_KEY)) {
                        config.set(WORLD_NAME_KEY, "world");
                    }
                    worldName = config.getString(WORLD_NAME_KEY);
                    break;
                case MOVE_SERVER:
                    if (!config.contains(SERVER_NAME_KEY)) {
                        config.set(SERVER_NAME_KEY, "lobby");
                    }
                    serverName = config.getString(SERVER_NAME_KEY);
                    break;
                default:
            }
        }

        super.initialize();
    }

    @Override
    protected List<String> getEnabledLore() {
        final List<String> parts = Lists.newArrayList();
        parts.addAll(messages.evalTemplates(
                ENABLED_LORE_PATH + ".header",
                ImmutableMap.of("delay", TimeUtil.secondsToString(delay))
        ));

        for (final BanType type : types) {
            switch (type) {
                case MOVE_WORLD:
                    parts.addAll(messages.evalTemplates(
                            ENABLED_LORE_PATH + ".actions.move world",
                            ImmutableMap.of("world", worldName)
                    ));
                    break;
                case BAN:
                    parts.addAll(messages.evalTemplates(
                            ENABLED_LORE_PATH + ".actions.ban",
                            ImmutableMap.of("duration", TimeUtil.secondsToString(duration))
                    ));
                    break;
                case MOVE_SERVER:
                    parts.addAll(messages.evalTemplates(
                            ENABLED_LORE_PATH + ".actions.server",
                            ImmutableMap.of("server", serverName)
                    ));
                    break;
                case KICK:
                    parts.addAll(messages.getRawStrings(ENABLED_LORE_PATH + ".actions.kick"));
                    break;
                default:
            }
        }

        return parts;
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        final BukkitRunnable waiting = timers.get(event.getPlayer().getUniqueId());

        if (waiting == null) return;

        // run it now and remove the timer
        waiting.run();
        timers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        final UUID uuid = event.getEntity().getUniqueId();

        // skip if there is already a timer running
        if (timers.containsKey(uuid)) return;

        final BanRunnable runnable = new BanRunnable(uuid);
        runnable.runTaskLater(plugin, delay * TICKS_PER_SECOND);
        timers.put(uuid, runnable);

        if (types.contains(BanType.MOVE_WORLD)) {
            final World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getLogger()
                        .severe("World for deathbans `" + worldName + "` does not exist, could not move player");
            } else {
                event.getEntity().setBedSpawnLocation(world.getSpawnLocation(), true);
            }
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    protected class BanRunnable extends BukkitRunnable {

        protected final UUID uuid;

        public BanRunnable(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public void run() {
            timers.remove(uuid);

            final Player player = Bukkit.getPlayer(uuid);
            player.sendMessage(message);

            if (types.contains(BanType.BAN)) {
                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, (int) duration);

                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), message, calendar.getTime(), "UHC");
            }

            if (types.contains(BanType.MOVE_SERVER)) {
                player.sendPluginMessage(plugin, "BungeeCord", ("Connect" + serverName).getBytes());
            }

            if (types.contains(BanType.KICK)) {
                player.kickPlayer(message);
            }
        }
    }
}
