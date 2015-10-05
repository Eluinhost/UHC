package gg.uhc.uhc.modules;

import gg.uhc.uhc.inventory.IconStack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

public abstract class Module {

    protected final IconStack icon = new IconStack(Material.BARRIER, 1);
    protected ConfigurationSection config;
    protected Plugin plugin;

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public void initialize(ConfigurationSection config) throws InvalidConfigurationException {
        this.config = config;
    }

    protected void saveConfig() {
        plugin.saveConfig();
    }

    public final IconStack getIconStack() {
        return icon;
    }
}
