package gg.uhc.uhc.modules.pvp;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

public class GlobalPVPModule extends DisableableModule {

    protected static final String ICON_NAME = "PVP";

    public GlobalPVPModule() {
        // TODO world white/blacklist

        this.iconName = ICON_NAME;
        this.icon.setType(Material.IRON_SWORD);
        this.icon.setWeight(-5);
    }

    @Override
    public void rerender() {
        super.rerender();
        icon.setLore(isEnabled() ? "PVP is enabled in all worlds" : "PVP is disabled in all worlds");
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            world.setPVP(true);
        }
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            world.setPVP(false);
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
