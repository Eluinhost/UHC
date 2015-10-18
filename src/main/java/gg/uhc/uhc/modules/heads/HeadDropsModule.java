package gg.uhc.uhc.modules.heads;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.text.NumberFormat;
import java.util.List;
import java.util.Random;

public class HeadDropsModule extends DisableableModule implements Listener {

    protected static final Random random = new Random();

    protected static final String ICON_NAME = "Head Drops";
    protected static final String HEAD_METADATA_KEY = "falling head";
    protected static final Material DUMMY_FALLING_HEAD_MATERIAL = Material.PUMPKIN;

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

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        if (random.nextDouble() < (1D - dropRate)) return;

        Player player = event.getEntity();

        // spawn a falling sand block with a dummy type
        // because skulls don't render correctly ingame
        FallingBlock head = player.getWorld().spawnFallingBlock(player.getEyeLocation(), DUMMY_FALLING_HEAD_MATERIAL, (byte) 0);

        // set player UUID and facing direction on the
        // entity metadata for use later
        head.setMetadata(HEAD_METADATA_KEY, new FixedMetadataValue(plugin, new FallingHeadMetadata(player.getUniqueId(), BlockFaceXZ.getClosest(player))));

        head.setCustomName(player.getName());
        head.setCustomNameVisible(true);

        // use the player's velocity as a base
        Vector velocity = player.getVelocity().clone().multiply(0.75D);
        // add some vertical velocity
        velocity.add(new Vector(0D, .2D, 0D));

        head.setVelocity(velocity);
    }

    @EventHandler
    public void on(ItemSpawnEvent event) {
        if (!isEnabled()) return;

        if (event.getEntity().getItemStack().getType() != DUMMY_FALLING_HEAD_MATERIAL) return;

        // grab the first falling block entity at the same lcoation
        Entity entity = Iterables.getFirst(Iterables.filter(event.getEntity().getNearbyEntities(0, 0, 0), Predicates.instanceOf(FallingBlock.class)), null);

        // no entity found, do nothing
        if (entity == null) return;

        // grab the metadata for the block and replace the drop
        for (MetadataValue v : entity.getMetadata(HEAD_METADATA_KEY)) {
            if (v.getOwningPlugin().equals(plugin)) {
                FallingHeadMetadata data = (FallingHeadMetadata) v.value();
                event.getEntity().setItemStack(playerHeadProvider.getPlayerHeadItem(Bukkit.getPlayer(data.getUuid()).getName()));
                return;
            }
        }
    }

    @EventHandler
    public void on(EntityChangeBlockEvent event) {
        if (!isEnabled()) return;

        if (event.getTo() != DUMMY_FALLING_HEAD_MATERIAL) return;

        if (event.getEntityType() != EntityType.FALLING_BLOCK) return;

        List<MetadataValue> values = event.getEntity().getMetadata(HEAD_METADATA_KEY);

        for (MetadataValue v : values) {
            if (v.getOwningPlugin().equals(plugin)) {
                // cancel the event
                event.setCancelled(true);

                // replace the block with the head
                FallingHeadMetadata data = (FallingHeadMetadata) v.value();
                playerHeadProvider.setBlockAsHead(Bukkit.getPlayer(data.getUuid()).getName(), event.getBlock(), data.getDirection());

                return;
            }
        }
    }
}
