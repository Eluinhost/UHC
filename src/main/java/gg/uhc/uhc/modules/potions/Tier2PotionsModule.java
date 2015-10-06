package gg.uhc.uhc.modules.potions;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Tier2PotionsModule extends DisableableModule {

    protected static final String ICON_NAME = "Tier 2 Potion Brewing";

    protected final PotionFuelsListener listener;

    public Tier2PotionsModule(PotionFuelsListener listener) {
        this.listener = listener;

        Potion potion = new Potion(PotionType.INSTANT_HEAL);
        potion.setLevel(2);

        this.icon.setData(potion.toItemStack(1).getData());
        this.iconName = ICON_NAME;
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Tier 2 potions are brewable");
        } else {
            icon.setLore("Disabled tier 2 potion brewing");
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public void onEnable() {
        listener.removeMaterial(Material.GLOWSTONE_DUST);
    }

    @Override
    public void onDisable() {
        listener.addMaterial(Material.GLOWSTONE_DUST);
    }
}
