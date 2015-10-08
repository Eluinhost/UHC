package gg.uhc.uhc.modules.potions;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class WitchesModule extends DisableableModule {

    protected static final String ICON_NAME = "Witch spawns";

    public WitchesModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.FLOWER_POT_ITEM);
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Witches can spawn");
        } else {
            icon.setLore("Witches cannot spawn");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(CreatureSpawnEvent event) {
        if (isEnabled()) return;

        if (event.getEntity().getType() == EntityType.WITCH)
            event.setCancelled(true);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
