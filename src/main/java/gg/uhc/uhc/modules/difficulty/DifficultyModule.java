package gg.uhc.uhc.modules.difficulty;

import com.google.common.collect.ImmutableList;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.WorldMatcher;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class DifficultyModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Server Difficulty";

    protected WorldMatcher worlds;

    public DifficultyModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.ARROW);
        this.icon.setWeight(-10);
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        worlds = new WorldMatcher(section, ImmutableList.of("world to not touch on enable/disable"), false);

        super.initialize(section);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("All worlds are HARD difficulty");
        } else {
            icon.setLore("World difficulties are not handled by the plugin");
        }
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            if (worlds.worldMatches(world)) {
                world.setDifficulty(Difficulty.HARD);
            }
        }
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        if (isEnabled()) {
            if (worlds.worldMatches(event.getWorld())) {
                event.getWorld().setDifficulty(Difficulty.HARD);
            }
        }
    }
}
