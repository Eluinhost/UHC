/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.death.DeathStandsModule
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.modules.death;

import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeathStandsModule extends DisableableModule implements Listener {

    protected static final int DEATH_ANIMATION_TIME = 18;
    protected static final int TICKS_PER_SECOND = 20;
    protected static final double PLAYER_VELOCITY_MULTIPLIER = 1.5D;
    protected static final double PLAYER_VELOICY_Y_ADDITIONAL = .2D;

    protected static final Predicate<ItemStack> EMPTY_ITEM = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack input) {
            return input == null || input.getType() == Material.AIR;
        }
    };

    protected static final String ICON_NAME = "Death armour stands";
    protected static final String STAND_PREFIX = ChatColor.RED + "RIP: " + ChatColor.RESET;

    public DeathStandsModule() {
        setId("DeathStands");

        this.iconName = ICON_NAME;

        this.icon.setType(Material.ARMOR_STAND);
        this.icon.setWeight(ModuleRegistry.CATEGORY_DEATH);
    }

    protected boolean isProtectedArmourStand(Entity entity) {
        final String customName = entity.getCustomName();

        return customName != null && customName.startsWith(STAND_PREFIX);
    }

    @SuppressWarnings("Duplicates")
    protected Map<EquipmentSlot, ItemStack> getItems(ArmorStand stand) {
        final Map<EquipmentSlot, ItemStack> slots = Maps.newHashMapWithExpectedSize(5);

        slots.put(EquipmentSlot.HAND, stand.getItemInHand());
        slots.put(EquipmentSlot.HEAD, stand.getHelmet());
        slots.put(EquipmentSlot.CHEST, stand.getChestplate());
        slots.put(EquipmentSlot.LEGS, stand.getLeggings());
        slots.put(EquipmentSlot.FEET, stand.getBoots());

        return slots;
    }

    @SuppressWarnings("Duplicates")
    protected Map<EquipmentSlot, ItemStack> getItems(PlayerInventory inventory) {
        final Map<EquipmentSlot, ItemStack> slots = Maps.newHashMapWithExpectedSize(5);

        slots.put(EquipmentSlot.HAND, inventory.getItemInHand());
        slots.put(EquipmentSlot.HEAD, inventory.getHelmet());
        slots.put(EquipmentSlot.CHEST, inventory.getChestplate());
        slots.put(EquipmentSlot.LEGS, inventory.getLeggings());
        slots.put(EquipmentSlot.FEET, inventory.getBoots());

        return slots;
    }

    protected EnumMap<EquipmentSlot, ItemStack> getSavedSlots(Player player) {
        for (final MetadataValue value : player.getMetadata(StandItemsMetadata.KEY)) {
            if (!(value instanceof StandItemsMetadata)) continue;

            // remove the metadata
            player.removeMetadata(StandItemsMetadata.KEY, value.getOwningPlugin());

            // return the map
            return ((StandItemsMetadata) value).value();
        }

        return Maps.newEnumMap(EquipmentSlot.class);
    }

    protected void removeFirstEquals(Iterable iterable, Object equal) {
        final Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(equal)) {
                iterator.remove();
                return;
            }
        }
    }

    // run at high so previous events can modify the drops before we do (HeadDrops)
    @EventHandler(priority = EventPriority.HIGH)
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        final Player player = event.getEntity();

        // make the player invisible for the duration of their death animation
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, DEATH_ANIMATION_TIME, 1));

        final Location location = player.getLocation();

        // create an armour stand at the player
        final ArmorStand stand = player.getWorld().spawn(location.clone().add(0, .2D, 0), ArmorStand.class);
        stand.setBasePlate(false);
        stand.setArms(true);

        // give the armour stand the death message as a name
        stand.setCustomName(STAND_PREFIX + event.getDeathMessage());
        stand.setCustomNameVisible(true);

        // face the same direction as the player
        stand.getLocation().setDirection(location.getDirection());

        // set the armour stand helmet to be looking at the same yaw
        stand.setHeadPose(new EulerAngle(Math.toRadians(location.getPitch()), 0, 0));

        // use the player's velocity as a base and apply it to the stand
        stand.setVelocity(
                player.getVelocity()
                        .clone()
                        .multiply(PLAYER_VELOCITY_MULTIPLIER)
                        .add(new Vector(0D, PLAYER_VELOICY_Y_ADDITIONAL, 0D))
        );

        // start with player's existing items in each slot (if exists)
        Map<EquipmentSlot, ItemStack> toSet = getItems(player.getInventory());

        // overide with any saved items in the metadata
        toSet.putAll(getSavedSlots(player));

        // filter out the invalid items
        toSet = Maps.filterValues(toSet, Predicates.not(EMPTY_ITEM));

        final List<ItemStack> drops = event.getDrops();

        for (final Map.Entry<EquipmentSlot, ItemStack> entry : toSet.entrySet()) {
            final ItemStack stack = entry.getValue();

            if (stack == null) continue;

            // remove the first matching stack in the drop list
            removeFirstEquals(drops, stack);

            // set the item on the armour stand in the correct slot
            switch (entry.getKey()) {
                case HAND:
                    stand.setItemInHand(stack);
                    break;
                case HEAD:
                    stand.setHelmet(stack);
                    break;
                case CHEST:
                    stand.setChestplate(stack);
                    break;
                case LEGS:
                    stand.setLeggings(stack);
                    break;
                case FEET:
                    stand.setBoots(stack);
                    break;
                default:
            }
        }
    }

    @EventHandler
    public void on(PlayerArmorStandManipulateEvent event) {
        final ArmorStand stand = event.getRightClicked();

        if (!isProtectedArmourStand(stand)) return;

        final ItemStack players = event.getPlayerItem();
        final ItemStack stands = event.getArmorStandItem();

        // if the player is holding something it will be a swap
        if (players == null || players.getType() != Material.AIR) return;

        // if the stand hasn't got something then the player is adding
        // items or nothing will happen
        if (stands == null || stands.getType() == Material.AIR) return;

        // they're removing an item from the armour stand. If there
        // is only 1 item on the stand then this is the final item
        // on the armour stand so kill it (fire optional)
        if (Maps.filterValues(getItems(stand), Predicates.not(EMPTY_ITEM)).values().size() == 1)  {
            stand.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND) return;

        if (!isProtectedArmourStand(event.getEntity())) return;

        // always cancel events, we choose when to break the stand
        event.setCancelled(true);

        final ArmorStand stand = (ArmorStand) event.getEntity();
        final Location loc = stand.getLocation();
        final World world = stand.getWorld();

        // for the first 2 seconds don't allow breaking
        // to avoid accidental breaks after kill
        if (event.getEntity().getTicksLived() < 2 * TICKS_PER_SECOND) {
            world.playEffect(stand.getEyeLocation(), Effect.WITCH_MAGIC, 0);
            return;
        }

        // drop each of it's worn items
        for (final ItemStack stack : Maps.filterValues(getItems(stand), Predicates.not(EMPTY_ITEM)).values()) {
            world.dropItemNaturally(loc, stack);
        }

        // kill the stand now
        stand.remove();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
