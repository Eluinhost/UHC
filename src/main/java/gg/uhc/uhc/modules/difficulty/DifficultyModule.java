package gg.uhc.uhc.modules.difficulty;

import gg.uhc.uhc.inventory.IconStack;
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

    public DifficultyModule(IconStack icon, boolean enabled) {
        super(ICON_NAME, icon, enabled);

        // TODO whitelist/blacklist

        icon.setType(Material.ARROW);
    }

    @Override
    public void onEnable() {
        icon.setLore("All worlds are HARD difficulty");

        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.HARD);
        }
    }

    @Override
    public void onDisable() {
        icon.setLore("World difficulties are not handled by the plugin");
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        if (isEnabled()) {
            event.getWorld().setDifficulty(Difficulty.HARD);
        }
    }
}
