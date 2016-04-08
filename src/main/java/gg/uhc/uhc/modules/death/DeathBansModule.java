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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.uhc.flagcommands.converters.EnumConverter;
import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.util.TimeUtil;
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
        if (!config.contains("action")) {
            config.set("action", "BAN+KICK");
        }

        if (!config.contains("message")) {
            config.set("message", "RIP");
        }
        this.message = config.getString("message");

        if (!config.contains("delay seconds")) {
            config.set("delay seconds", 20);
        }
        this.delay = config.getInt("delay seconds");

        ImmutableSet.Builder<BanType> types = ImmutableSet.builder();
        for (String banTypeString : config.getString("action").split("\\+")) {
            try {
                types.add(BAN_PARSER.convert(banTypeString));
            } catch (ValueConversionException ex) {
                throw new InvalidConfigurationException("Invalid ban type given", ex);
            }
        }

        this.types = types.build();

        // ensure extra fields are set
        for (BanType type : this.types) {
            switch (type) {
                case BAN:
                    if (!config.contains("duration")) {
                        config.set("duration", "1d");
                    }
                    duration = TimeUtil.getSeconds(config.getString("duration"));
                case MOVE_WORLD:
                    if (!config.contains("world name")) {
                        config.set("world name", "world");
                    }
                    worldName = config.getString("world name");
                    break;
                case MOVE_SERVER:
                    if (!config.contains("server name")) {
                        config.set("server name", "lobby");
                    }
                    serverName = config.getString("server name");
                    break;
            }
        }

        super.initialize();
    }

    @Override
    protected List<String> getEnabledLore() {
        List<String> parts = Lists.newArrayList();
        parts.addAll(messages.evalTemplates(ENABLED_LORE_PATH + ".header", ImmutableMap.of("delay", TimeUtil.secondsToString(delay))));

        for (BanType type : types) {
            switch (type) {
                case MOVE_WORLD:
                    parts.addAll(messages.evalTemplates(ENABLED_LORE_PATH + ".actions.move world", ImmutableMap.of("world", worldName))); break;
                case BAN:
                    parts.addAll(messages.evalTemplates(ENABLED_LORE_PATH + ".actions.ban", ImmutableMap.of("duration", TimeUtil.secondsToString(duration)))); break;
                case MOVE_SERVER:
                    parts.addAll(messages.evalTemplates(ENABLED_LORE_PATH + ".actions.server", ImmutableMap.of("server", serverName))); break;
                case KICK:
                    parts.addAll(messages.getRawStrings(ENABLED_LORE_PATH + ".actions.kick")); break;
            }
        }

        return parts;
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        BukkitRunnable waiting = timers.get(event.getPlayer().getUniqueId());

        if (waiting == null) return;

        // run it now and remove the timer
        waiting.run();
        timers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        UUID uuid = event.getEntity().getUniqueId();

        // skip if there is already a timer running
        if (timers.containsKey(uuid)) return;

        BanRunnable runnable = new BanRunnable(uuid);
        runnable.runTaskLater(plugin, delay * 20);
        timers.put(uuid, runnable);

        if (types.contains(BanType.MOVE_WORLD)) {
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getLogger().severe("World for deathbans `" + worldName + "` does not exist, could not move player");
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

            Player player = Bukkit.getPlayer(uuid);
            player.sendMessage(message);

            if (types.contains(BanType.BAN)) {
                Calendar calendar = Calendar.getInstance();
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
