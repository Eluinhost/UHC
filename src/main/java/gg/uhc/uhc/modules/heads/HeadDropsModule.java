package gg.uhc.uhc.modules.heads;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HeadDropsModule extends DisableableModule implements Listener {

    protected static final Random random = new Random();

    protected static final String ICON_NAME = "Head Drops";
    protected static final String STAND_PREFIX = ChatColor.RED + "RIP: " + ChatColor.RESET;

    protected static final NumberFormat formatter = NumberFormat.getNumberInstance();

    static {
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(1);
    }

    protected final PlayerHeadProvider playerHeadProvider;
    protected double dropRate = 0;

    public HeadDropsModule(PlayerHeadProvider playerHeadProvider) {
        this.playerHeadProvider = playerHeadProvider;
        this.iconName = ICON_NAME;
        this.icon.setType(Material.SKULL_ITEM);
        this.icon.setDurability((short) 3);
        this.icon.setWeight(-5);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        if (!section.contains("drop chance")) {
            section.set("drop chance", 100D);
        }

        if (!section.isDouble("drop chance") && !section.isInt("drop chance"))
            throw new InvalidConfigurationException("Invalid value at " + section.getCurrentPath() + ".drop chance (" + section.get("drop chance"));

        dropRate = section.getDouble("drop chance") / 100D;

        super.initialize(section);
    }

    public double getDropRate() {
        return dropRate;
    }

    public void setDropRate(double rate) {
        Preconditions.checkArgument(rate >= 0D && rate <= 1D);
        this.dropRate = rate;
        config.set("drop chance", this.dropRate);
        saveConfig();
        rerender();
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore(ChatColor.GREEN + "Drop rate: " + formatter.format(dropRate * 100) + "%");
        } else {
            icon.setLore("Heads do not drop");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        if (random.nextDouble() < (1D - dropRate)) return;

        Player player = event.getEntity();
        Location location = player.getLocation();

        // make the player invisible for the duration of the death animation
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 18, 1));

        ArmorStand stand = player.getWorld().spawn(location.clone().add(0, .2D, 0), ArmorStand.class);
        stand.setBasePlate(false);
        stand.setArms(true);

        // give the player's name
        stand.setCustomName(STAND_PREFIX + event.getDeathMessage());
        stand.setCustomNameVisible(true);

        // face the same direction as the player
        stand.getLocation().setDirection(location.getDirection());

        // set the armour stand helmet to the player's head looking at the same yaw
        stand.setHelmet(playerHeadProvider.getPlayerHeadItem(player));
        stand.setHeadPose(new EulerAngle(Math.toRadians(location.getPitch()), 0, 0));

        // copy the player's items across
        PlayerInventory inventory = player.getInventory();
        final List<ItemStack> toRemove = Lists.newArrayListWithCapacity(4);

        ItemStack chest = inventory.getChestplate();
        if (chest != null) {
            stand.setChestplate(chest);
            toRemove.add(chest);
        }

        ItemStack leggings = inventory.getLeggings();
        if (leggings != null) {
            stand.setLeggings(leggings);
            toRemove.add(leggings);
        }

        ItemStack boots = inventory.getBoots();
        if (boots != null) {
            stand.setBoots(boots);
            toRemove.add(boots);
        }

        ItemStack hand = player.getItemInHand();
        if (hand != null) {
            stand.setItemInHand(hand);
            toRemove.add(hand);
        }

        // stop copied items from appearing in the final drops
        Iterables.removeIf(event.getDrops(), new Predicate<ItemStack>() {
            @Override
            public boolean apply(ItemStack input) {
                int index = toRemove.indexOf(input);

                if (index < 0) return false;

                toRemove.remove(index);
                return true;
            }
        });

        // use the player's velocity as a base
        stand.setVelocity(player.getVelocity().clone().multiply(1.5D).add(new Vector(0D, .2D, 0D)));
    }

    protected boolean isProtectedArmourStand(Entity entity) {
        String customName = entity.getCustomName();

        return customName != null && customName.startsWith(STAND_PREFIX);
    }

    protected Map<EquipmentSlot, ItemStack> getItemMap(ArmorStand stand) {
        return ImmutableMap.<EquipmentSlot, ItemStack>builder()
                .put(EquipmentSlot.HAND, stand.getItemInHand())
                .put(EquipmentSlot.HEAD, stand.getHelmet())
                .put(EquipmentSlot.CHEST, stand.getChestplate())
                .put(EquipmentSlot.LEGS, stand.getLeggings())
                .put(EquipmentSlot.FEET, stand.getBoots())
                .build();
    }

    protected Collection<ItemStack> getAllItems(ArmorStand stand) {
        return Collections2.filter(getItemMap(stand).values(), Predicates.not(IS_AIR_ITEM));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND) return;

        if (!isProtectedArmourStand(event.getEntity())) return;

        // always cancel events, we choose when to break the stand
        event.setCancelled(true);

        ArmorStand stand = (ArmorStand) event.getEntity();
        Location loc = stand.getLocation();
        World world = stand.getWorld();

        // for the first 2 seconds don't allow breaking
        // to avoid accidental breaks after kill
        if (event.getEntity().getTicksLived() < 40) {
            world.playEffect(stand.getEyeLocation(), Effect.WITCH_MAGIC, 0);
            return;
        }

        // drop each of it's worn items
        for (ItemStack stack : getAllItems(stand)) {
            world.dropItemNaturally(loc, stack);
        }

        // kill the stand now
        stand.remove();
    }

    @EventHandler
    public void on(PlayerArmorStandManipulateEvent event) {
        ArmorStand stand = event.getRightClicked();

        if (!isProtectedArmourStand(stand)) return;

        ItemStack players = event.getPlayerItem();
        ItemStack stands = event.getArmorStandItem();

        // if the player is holding something it will be a swap
        if (players == null || players.getType() != Material.AIR) return;

        // if the stand hasn't got something then the player is adding
        // items or nothing will happen
        if (stands == null || stands.getType() == Material.AIR) return;

        // they're removing an item from the armour stand. If there
        // is only 1 item on the stand then this is the final item
        // on the armour stand so kill it (fire optional)
        if (getAllItems(stand).size() == 1)  {
            stand.remove();
        }
    }

    protected static final Predicate<ItemStack> IS_AIR_ITEM = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack input) {
            return input != null && input.getType() == Material.AIR;
        }
    };
}
