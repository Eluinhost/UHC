package gg.uhc.uhc.modules.potions;

import gg.uhc.uhc.modules.DisableableModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class SplashPotionsModule extends DisableableModule {

    protected static final String ICON_NAME = "Splash Potion Brewing";

    protected final PotionFuelsListener listener;

    public SplashPotionsModule(PotionFuelsListener listener) {
        this.listener = listener;

        Potion potion = new Potion(PotionType.POISON);
        potion.setSplash(true);

        this.icon.setType(Material.POTION);
        this.icon.setDurability(potion.toDamageValue());
        this.iconName = ICON_NAME;
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Splash potions are brewable");
        } else {
            icon.setLore("Disabled splash potion brewing");
        }
    }

    @Override
    public void onEnable() {
        listener.removeMaterial(Material.SULPHUR);
    }

    @Override
    public void onDisable() {
        listener.addMaterial(Material.SULPHUR, ChatColor.RED + "Splash potions are unbrewable.");
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }
}
