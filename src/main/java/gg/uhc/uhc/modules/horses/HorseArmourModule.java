package gg.uhc.uhc.modules.horses;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import gg.uhc.uhc.modules.DisableableModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Set;

public class HorseArmourModule extends DisableableModule implements Listener {

    public static final String ICON_NAME = "Horse Armour";

    protected static final Set<Material> DISABLED = ImmutableSet.of(Material.IRON_BARDING, Material.GOLD_BARDING, Material.DIAMOND_BARDING);

    public HorseArmourModule() {
        this.iconName = ICON_NAME;

        this.icon.setType(Material.DIAMOND_BARDING);
    }

    @Override
    public void rerender() {
        super.rerender();

        icon.setLore(isEnabled() ? "Player ridden horses can wear armour" : "Player ridden horse have their armour removed");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isInsideVehicle()) {
                Entity vehicle = player.getVehicle();

                if (vehicle.getType() == EntityType.HORSE && removeHorseArmour((Horse) vehicle)) {
                    player.sendMessage(ChatColor.RED + "Dropped horse's armour on the ground as it is disabled");
                }
            }
        }
    }

    protected boolean removeHorseArmour(Horse horse) {
        ItemStack armour = horse.getInventory().getArmor();

        if (armour != null && armour.getType() != Material.AIR) {
            // remove the armour and drop it into the world
            horse.getInventory().setArmor(null);
            horse.getWorld().dropItemNaturally(horse.getLocation(), armour);
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityMountEvent event) {
        if (isEnabled() || event.getEntityType() != EntityType.PLAYER || event.getMount().getType() != EntityType.HORSE) return;

        if (removeHorseArmour((Horse) event.getMount())) {
            event.getEntity().sendMessage(ChatColor.RED + "Dropped horse's armour on the ground as it is disabled");
        }
    }

    // if you can drag armour for some reason
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent event) {
        if (isEnabled()) return;

        if (!(event.getView().getTopInventory() instanceof HorseInventory)) return;

        // if it's not a disabled type do nothing
        if (!DISABLED.contains(event.getOldCursor().getType())) return;

        event.getWhoClicked().sendMessage(ChatColor.RED + "Horse armour is disabled");
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        if (isEnabled()) return;

        if (!(event.getView().getTopInventory() instanceof HorseInventory)) return;

        // clicked outside of the window
        if (event.getClickedInventory() == null) return;

        InventoryType clicked = event.getClickedInventory().getType();

        // get any relevant stack to check the type of based on the action took
        Optional<ItemStack> relevant = Optional.absent();
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                // only worry about player -> horse
                if (clicked == InventoryType.PLAYER) {
                    relevant = Optional.fromNullable(event.getCurrentItem());
                }
                break;
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
                // only worry about within the horse
                if (clicked != InventoryType.PLAYER) {
                    relevant = Optional.fromNullable(event.getCursor());
                }
                break;
            case HOTBAR_SWAP:
                // only worry about within a horse
                if (clicked != InventoryType.PLAYER) {
                    relevant = Optional.fromNullable(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
                }
                break;
        }

        if (relevant.isPresent() && DISABLED.contains(relevant.get().getType())) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "Horse armour is disabled");
            event.setCancelled(true);
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }
}
