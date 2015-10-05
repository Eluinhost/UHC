package gg.uhc.uhc.modules.health;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class GhastTearDropsModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Ghast Tears";

    public GhastTearDropsModule() {
        this.iconName = ICON_NAME;
        this.icon.setWeight(5);
        // TODO world whitelist/blacklist with config
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setType(Material.GHAST_TEAR);
            icon.setLore("Ghasts drop ghast tears");
        } else {
            icon.setType(Material.GOLD_INGOT);
            icon.setLore("Ghasts drop gold ingots");
        }
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (isEnabled() || event.getEntity().getType() != EntityType.GHAST) return;

        for (ItemStack drop : event.getDrops()) {
            if (drop.getType() == Material.GHAST_TEAR) {
                drop.setType(Material.GOLD_INGOT);
            }
        }
    }
}
