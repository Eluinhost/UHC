package gg.uhc.uhc.modules.health;

import gg.uhc.uhc.inventory.ClickHandler;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class HealthRegenerationModule extends DisableableModule implements ClickHandler, Listener {

    protected static final String GAME_RULE = "naturalRegeneration";
    protected static final String ICON_NAME = "Health Regeneration";

    protected static final short ENABLED_DATA = new Potion(PotionType.REGEN).toDamageValue();
    protected static final short DISABLED_DATA = new Potion(PotionType.WATER).toDamageValue();

    public HealthRegenerationModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.POTION);
        this.icon.setWeight(-10);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setDurability(ENABLED_DATA);
            icon.setLore("Natural health regeneration is enabled");
        } else {
            icon.setDurability(DISABLED_DATA);
            icon.setLore("Natural health regeneration is disabled");
        }
    }

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue(GAME_RULE, "true");
        }
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue(GAME_RULE, "false");
        }
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        event.getWorld().setGameRuleValue(GAME_RULE, isEnabled() ? "true" : "false");
    }
}
