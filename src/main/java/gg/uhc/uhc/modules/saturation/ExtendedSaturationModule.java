package gg.uhc.uhc.modules.saturation;

import gg.uhc.uhc.inventory.IconStack;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;

public class ExtendedSaturationModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Extended Saturation";

    protected final Plugin plugin;
    protected float multiplier;

    public ExtendedSaturationModule(IconStack icon, boolean enabled, Plugin plugin, float multiplier) {
        super(ICON_NAME, icon, enabled);
        this.plugin = plugin;

        // TODO show multiplier in icon
        this.multiplier = multiplier;

        icon.setType(Material.COOKED_BEEF);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerItemConsumeEvent event) {
        new SaturationMultiplierRunnable(event.getPlayer().getUniqueId(), event.getPlayer().getSaturation(), multiplier).runTask(plugin);
    }
}
