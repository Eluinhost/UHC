package gg.uhc.uhc.modules.pvp;

import com.google.common.collect.ImmutableList;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.WorldMatcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class GlobalPVPModule extends DisableableModule {

    protected static final String ICON_NAME = "PVP";

    protected WorldMatcher worlds;

    public GlobalPVPModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.IRON_SWORD);
        this.icon.setWeight(-5);
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        worlds = new WorldMatcher(section, ImmutableList.of("world to not touch on enable/disable"), false);

        super.initialize(section);
    }

    @Override
    public void rerender() {
        super.rerender();
        icon.setLore(isEnabled() ? "PVP is enabled in all worlds" : "PVP is disabled in all worlds");
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            if (worlds.worldMatches(world)) {
                world.setPVP(true);
            }
        }
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            if (worlds.worldMatches(world)) {
                world.setPVP(false);
            }
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
