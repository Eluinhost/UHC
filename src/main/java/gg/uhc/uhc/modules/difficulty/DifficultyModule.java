package gg.uhc.uhc.modules.difficulty;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class DifficultyModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Server Difficulty";

    public DifficultyModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.ARROW);
        this.icon.setWeight(-10);
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
            world.setDifficulty(Difficulty.HARD);
        }
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        if (isEnabled()) {
            event.getWorld().setDifficulty(Difficulty.HARD);
        }
    }
}
