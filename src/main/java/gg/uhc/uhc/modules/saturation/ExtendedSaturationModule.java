package gg.uhc.uhc.modules.saturation;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ExtendedSaturationModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Extended Saturation";

    protected double multiplier;

    public ExtendedSaturationModule() {
        this.iconName = ICON_NAME;

        this.icon.setType(Material.COOKED_BEEF);
        this.icon.setWeight(10);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        config.set("multiplier", multiplier);
        saveConfig();
        rerender();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        if (!section.contains("multiplier")) {
            section.set("multiplier", 2.5D);
        }

        if (!section.isDouble("multiplier") && !section.isInt("multiplier"))
            throw new InvalidConfigurationException("Invalid value at " + section.getCurrentPath() + ".multiplier (" + section.get("multiplier") + ")");

        // TODO check sane values
        multiplier = section.getDouble("multiplier");
        super.initialize(section);
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            this.icon.setLore("Food gives " + multiplier + " times the saturation");
        } else {
            this.icon.setLore("Food gives regular levels of saturation");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerItemConsumeEvent event) {
        if (isEnabled()) {
            new SaturationMultiplierRunnable(event.getPlayer().getUniqueId(), event.getPlayer().getSaturation(), multiplier).runTask(plugin);
        }
    }
}
