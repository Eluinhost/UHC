package gg.uhc.uhc.modules.enderpearls;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EnderpearlsModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Enderpearl Damage";

    public EnderpearlsModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.ENDER_PEARL);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    protected void rerender() {
        super.rerender();

        icon.setLore(isEnabled() ? "Enderpearls do damage" : "Enderpearls do no damage");
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        if (isEnabled()) return;

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getDamager().getType() != EntityType.ENDER_PEARL) return;

        event.setCancelled(true);
    }
}
