package gg.uhc.uhc.modules.recipes;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class NotchApplesModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Notch apples";

    public NotchApplesModule() {
        this.iconName = ICON_NAME;

        // TODO allow permission?

        this.icon.setType(Material.GOLDEN_APPLE);
        this.icon.setDurability((short) 1);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Notch apples are craftable");
        } else {
            icon.setLore("Notch apples are uncraftable");
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (isEnabled()) return;

        Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.GOLDEN_APPLE) return;

        if (recipe.getResult().getDurability() == 1) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
