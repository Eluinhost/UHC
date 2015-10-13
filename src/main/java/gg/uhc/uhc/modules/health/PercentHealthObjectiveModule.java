package gg.uhc.uhc.modules.health;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.UUID;

public class PercentHealthObjectiveModule extends DisableableModule {

    protected static final String ICON_NAME = "Health Percent Objective";

    protected static final String OBJECTIVE_NAME_KEY = "objective name";
    protected static final String UPDATE_PERIOD_KEY = "update period";

    protected final Map<UUID, Double> trackedHealth = Maps.newHashMap();

    protected Optional<BukkitRunnable> task = Optional.absent();
    protected Objective objective;
    protected int updatePeriod;

    public PercentHealthObjectiveModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.DAYLIGHT_DETECTOR);
    }

    public Objective getObjective() {
        return objective;
    }

    public void updatePlayers() {
        double current;
        Double old;
        for (Player player : Bukkit.getOnlinePlayers()) {
            current = player.getHealth();
            old = trackedHealth.put(player.getUniqueId(), current);

            if (old == null || old != current) {
                objective.getScore(player).setScore((int) Math.ceil(current * 5));
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

        icon.setLore(isEnabled() ? "Player health objective (" + objective.getName() + ") is being updated" : "Health percent objective is not being updated");
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
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        if (!section.contains(OBJECTIVE_NAME_KEY)) {
            section.set(OBJECTIVE_NAME_KEY, "UHCHealthPercent");
        }

        if (!section.contains(UPDATE_PERIOD_KEY)) {
            section.set(UPDATE_PERIOD_KEY, 20);
        }

        updatePeriod = section.getInt(UPDATE_PERIOD_KEY);

        if (updatePeriod <= 0) throw new InvalidConfigurationException("Update period must be >= 1, provided: " + section.get(UPDATE_PERIOD_KEY));

        String objectiveName = section.getString(OBJECTIVE_NAME_KEY);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        objective = scoreboard.getObjective(objectiveName);

        if (objective == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy");
        }

        super.initialize(section);
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
