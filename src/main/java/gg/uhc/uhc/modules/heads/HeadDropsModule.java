package gg.uhc.uhc.modules.heads;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.death.StandItemsMetadata;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.Random;

public class HeadDropsModule extends DisableableModule implements Listener {

    protected static final Random random = new Random();
    protected static final String ICON_NAME = "Head Drops";

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

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        if (!isEnabled() || random.nextDouble() < (1D - dropRate)) {
            // set to an empty map to avoid stale metadata problems
            event.getEntity().setMetadata(StandItemsMetadata.KEY, new StandItemsMetadata(plugin));
            return;
        }

        Player player = event.getEntity();

        // create a head
        ItemStack head = playerHeadProvider.getPlayerHeadItem(player);

        // add it to the drops
        event.getDrops().add(head);

        // add metadata for the armour stand module to put the helmet on the player and remove from drops
        EnumMap<EquipmentSlot, ItemStack> standItems = Maps.newEnumMap(EquipmentSlot.class);
        standItems.put(EquipmentSlot.HEAD, head);

        player.setMetadata(StandItemsMetadata.KEY, new StandItemsMetadata(plugin, standItems));
    }
}
