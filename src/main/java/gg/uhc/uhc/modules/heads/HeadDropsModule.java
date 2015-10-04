package gg.uhc.uhc.modules.heads;

import com.google.common.base.Preconditions;
import gg.uhc.uhc.inventory.IconStack;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.text.NumberFormat;
import java.util.Random;

public class HeadDropsModule extends DisableableModule {

    protected static final Random random = new Random();
    protected static final String ICON_NAME = "Head Drops";
    protected static final NumberFormat formatter = NumberFormat.getNumberInstance();
    static {
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(1);
    }

    protected final PlayerHeadProvider playerHeadProvider;
    protected float dropRate = 0;

    public HeadDropsModule(IconStack icon, boolean enabled, float dropRate, PlayerHeadProvider playerHeadProvider) {
        super(ICON_NAME, icon, enabled);
        this.playerHeadProvider = playerHeadProvider;

        // TODO PVP only drops + team drops

        icon.setType(Material.SKULL_ITEM);
        icon.setDurability((short) 3);

        setDropRate(dropRate);
    }

    public void setDropRate(float rate) {
        Preconditions.checkArgument(rate >= 0F && rate <= 1F);
        this.dropRate = rate;

        updateIconInfo();
    }

    @Override
    public void onEnable() {
        updateIconInfo();
    }

    @Override
    public void onDisable() {
        updateIconInfo();
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        if (random.nextInt(100) < (100 - dropRate)) return;

        // TODO stake + place blocks e.t.c.

        event.getDrops().add(playerHeadProvider.getPlayerHeadItem(event.getEntity()));
    }

    protected void updateIconInfo() {
        if (enabled) {
            setLore(ChatColor.GREEN + "Drop rate: " + formatter.format(dropRate * 100) + "%");
        } else {
            setLore("Heads do not drop");
        }

        icon.setAmount(isEnabled() ? Math.round(dropRate * 10) : 0);
    }
}
