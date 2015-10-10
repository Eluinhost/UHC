package gg.uhc.uhc.modules.autorespawn;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AutoRespawnModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Autorespawn on Death";

    public AutoRespawnModule() {
        this.icon.setType(Material.REDSTONE);
        this.icon.setWeight(50);
        this.iconName = ICON_NAME;
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("You will automatically respawn on death");
        } else {
            icon.setLore("Autorespawn is disabled");
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        new PlayerRespawnRunnable(event.getEntity()).runTaskLater(plugin, 18);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
