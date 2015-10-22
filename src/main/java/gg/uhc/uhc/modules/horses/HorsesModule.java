package gg.uhc.uhc.modules.horses;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorsesModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Horse Riding";

    public HorsesModule() {
        this.iconName = ICON_NAME;

        this.icon.setType(Material.MONSTER_EGG);
        this.icon.setDurability(EntityType.HORSE.getTypeId());
    }

    @Override
    public void rerender() {
        super.rerender();

        icon.setLore(isEnabled() ? "Riding horses is allowed" : "Riding horses is disabled");
    }

    protected void kickOffHorse(Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle == null) return;

        vehicle.eject();
        player.sendMessage(ChatColor.RED + "You were removed from your horse because horses are disabled");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            kickOffHorse(player);
        }
    }

    // also called when a player joins the game and is re-mounted
    @EventHandler
    public void on(EntityMountEvent event) {
        if (isEnabled() || event.getEntityType() != EntityType.PLAYER) return;

        if (event.getMount().getType() == EntityType.HORSE) {
            event.setCancelled(true);
            event.getEntity().sendMessage(ChatColor.RED + "Horses are disabled");
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
