package gg.uhc.uhc.modules.potions;

import gg.uhc.uhc.inventory.IconStack;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public class AbsorptionModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Absorption";

    protected final Plugin plugin;

    public AbsorptionModule(Plugin plugin, IconStack icon, boolean enabled) {
        super(ICON_NAME, icon, enabled);
        this.plugin = plugin;

        // TODO allow permission?

        icon.setType(Material.POTION);
    }

    @Override
    public void onEnable() {
        setLore("Absorption is enabled");
    }

    @Override
    public void onDisable() {
        setLore("Absorption is disabled");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerItemConsumeEvent event) {
        if (isEnabled()) return;

        if (event.getItem().getType() != Material.GOLDEN_APPLE) return;

        // schedule on next tick
        new RemovePotionEffectRunnable(event.getPlayer().getUniqueId(), PotionEffectType.ABSORPTION).runTask(plugin);
    }
}
