package gg.uhc.uhc.modules.recipes;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

public class GlisteringMelonRecipeModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Glistering Melon Recipe";

    public GlisteringMelonRecipeModule() {
        this.iconName = ICON_NAME;

        // TODO allow permission?

        this.icon.setType(Material.SPECKLED_MELON);
        this.icon.setWeight(10);

        ShapelessRecipe modified = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1))
                .addIngredient(1, Material.GOLD_BLOCK)
                .addIngredient(1, Material.MELON);

        Bukkit.addRecipe(modified);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    protected void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Requires a golden block to craft");
        } else {
            icon.setLore("Requires 8 golden nuggets to craft");
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.SPECKLED_MELON) return;

        if (RecipeUtil.hasRecipeGotMaterial(recipe, isEnabled() ? Material.GOLD_NUGGET : Material.GOLD_BLOCK)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
