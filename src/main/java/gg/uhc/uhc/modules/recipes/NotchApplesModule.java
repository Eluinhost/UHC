package gg.uhc.uhc.modules.recipes;

import gg.uhc.uhc.inventory.IconStack;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class NotchApplesModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Notch apples";

    public NotchApplesModule(IconStack icon, boolean enabled) {
        super(ICON_NAME, icon, enabled);

        // TODO allow permission?

        icon.setType(Material.GOLDEN_APPLE);
        icon.setDurability((short) 1);
    }

    @Override
    public void onEnable() {
        setLore("Notch apples are craftable");
    }

    @Override
    public void onDisable() {
        setLore("Notch apples are uncraftable");
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
