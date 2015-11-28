/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.health.PercentHealthObjectiveModule
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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PercentHealthObjectiveModule extends DisableableModule {

    protected static final String ICON_NAME = "Health Percent Objective";

    protected static final String OBJECTIVE_NAME_KEY = "objective name";
    protected static final String OBJECTIVE_DISPLAY_NAME_KEY = "objective display name";
    protected static final String OBJECTIVE_SCALING_KEY = "scaling";
    protected static final String UPDATE_PERIOD_KEY = "update period";

    protected final Map<UUID, Double> trackedHealth = Maps.newHashMap();

    protected Optional<BukkitRunnable> task = Optional.absent();
    protected Map<Objective, Integer> objectives;
    protected int updatePeriod;

    public PercentHealthObjectiveModule() {
        setId("PercentHealth");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.DAYLIGHT_DETECTOR);
        this.icon.setWeight(ModuleRegistry.CATEGORY_HEALTH);
    }

    public void updatePlayers() {
        Double current;
        Double old;
        for (Player player : Bukkit.getOnlinePlayers()) {
            current = player.getHealth();
            old = trackedHealth.put(player.getUniqueId(), current);

            if (!current.equals(old)) {
                for (Map.Entry<Objective, Integer> objective : objectives.entrySet()) {
                    objective.getKey().getScore(player.getName()).setScore((int) Math.ceil(current * objective.getValue()));
                }
            }
        }
    }

    protected void cancelTask() {
        if (!task.isPresent()) return;

        task.get().cancel();
        task = Optional.absent();
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            List<String> lore = Lists.newArrayList();
            lore.add(messages.getRaw("enabled lore.header"));

            for (Objective objective : objectives.keySet()) {
                lore.add(messages.evalTemplate("enabled lore.item", ImmutableMap.of("objective", objective.getName())));
            }

            icon.setLore(lore.toArray(new String[lore.size()]));
        } else {
            icon.setLore(messages.getRaw("disabled lore"));
        }
    }

    @Override
    public void onEnable() {
        cancelTask();

        HealthUpdateRunnable runnable = new HealthUpdateRunnable();
        runnable.runTaskTimer(plugin, 0, updatePeriod);
        task = Optional.<BukkitRunnable>of(runnable);
    }

    @Override
    public void onDisable() {
        cancelTask();
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        // setup default list if there isn't one
        if (!config.contains("objectives")) {
            // default for under player name
            ConfigurationSection name = new MemoryConfiguration();
            name.set(OBJECTIVE_NAME_KEY, "UHCHealthName");
            name.set(OBJECTIVE_DISPLAY_NAME_KEY, "&c&h");
            name.set(OBJECTIVE_SCALING_KEY, 5);

            // default for player list
            ConfigurationSection list = new MemoryConfiguration();
            list.set(OBJECTIVE_NAME_KEY, "UHCHealthList");
            list.set(OBJECTIVE_DISPLAY_NAME_KEY, "Health");
            list.set(OBJECTIVE_SCALING_KEY, 5);

            config.set("objectives", Lists.newArrayList(name, list));
        }

        if (!config.contains(UPDATE_PERIOD_KEY)) {
            config.set(UPDATE_PERIOD_KEY, 20);
        }

        List objectivesSpecs = config.getList("objectives");

        objectives = Maps.newHashMap();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (Object objectiveSpecObject : objectivesSpecs) {
            ConfigurationSection objectiveSpec;

            if (objectiveSpecObject instanceof Map) {
                MemoryConfiguration mc = new MemoryConfiguration();
                mc.addDefaults((Map) objectiveSpecObject);
                objectiveSpec = mc;
            } else {
                objectiveSpec = (ConfigurationSection) objectiveSpecObject;
            }

            if (!objectiveSpec.contains(OBJECTIVE_NAME_KEY)) {
                throw new InvalidConfigurationException("Missing required parameter `" + OBJECTIVE_NAME_KEY + "` for a percent health objective");
            }

            if (!objectiveSpec.contains(OBJECTIVE_DISPLAY_NAME_KEY)) {
                throw new InvalidConfigurationException("Missing required parameter `" + OBJECTIVE_DISPLAY_NAME_KEY + "` for a percent health objective");
            }

            String objectiveName = objectiveSpec.getString(OBJECTIVE_NAME_KEY);

            // translate colours with an extra &h for a heart icon
            String displayName = ChatColor.translateAlternateColorCodes('&', objectiveSpec.getString(OBJECTIVE_DISPLAY_NAME_KEY)).replace("&h", "♥");

            Integer scaling = objectiveSpec.contains(OBJECTIVE_SCALING_KEY) ? objectiveSpec.getInt(OBJECTIVE_SCALING_KEY) : 5;

            Objective objective = scoreboard.getObjective(objectiveName);

            // check for an invalid type and reregister it
            if (objective != null && !"dummy".equals(objective.getCriteria())) {
                plugin.getLogger().severe("Percent health objective '" + objectiveName + "' was registered as " + objective.getCriteria() + " instead of dummy, reregistering it");
                objective.unregister();
                objective = null;
            }

            // register a new one if needed
            if (objective == null) {
                objective = scoreboard.registerNewObjective(objectiveName, "dummy");
            }

            objective.setDisplayName(displayName);

            objectives.put(objective, scaling);
        }

        updatePeriod = config.getInt(UPDATE_PERIOD_KEY);

        if (updatePeriod <= 0) throw new InvalidConfigurationException("Update period must be >= 1, provided: " + config.get(UPDATE_PERIOD_KEY));

        super.initialize();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        trackedHealth.remove(event.getPlayer().getUniqueId());
    }

    class HealthUpdateRunnable extends BukkitRunnable {
        @Override
        public void run() {
            updatePlayers();
        }
    }
}
