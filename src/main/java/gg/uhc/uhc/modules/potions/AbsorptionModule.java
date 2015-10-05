package gg.uhc.uhc.modules.potions;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;

public class AbsorptionModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Absorption";

    public AbsorptionModule() {
        this.iconName = ICON_NAME;

        // TODO allow permission?

        this.icon.setType(Material.POTION);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Absorption is enabled");
        } else {
            icon.setLore("Absorption is disabled");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerItemConsumeEvent event) {
        if (isEnabled()) return;

        if (event.getItem().getType() != Material.GOLDEN_APPLE) return;

        // schedule on next tick
        new RemovePotionEffectRunnable(event.getPlayer().getUniqueId(), PotionEffectType.ABSORPTION).runTask(plugin);
    }
}
