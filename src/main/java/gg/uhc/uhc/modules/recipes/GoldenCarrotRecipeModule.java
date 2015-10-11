package gg.uhc.uhc.modules.recipes;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class GoldenCarrotRecipeModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Golden Carrot Recipe";

    public GoldenCarrotRecipeModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.GOLDEN_CARROT);
        this.icon.setWeight(10);

        // register the new recipe
        ShapedRecipe modified = new ShapedRecipe(new ItemStack(Material.GOLDEN_CARROT, 1))
                .shape("AAA", "ABA", "AAA")
                .setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.CARROT_ITEM);

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
            icon.setLore("Requires golden ingots to craft");
        } else {
            icon.setLore("Requires golden nuggets to craft");
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.GOLDEN_CARROT) return;

        if (RecipeUtil.hasRecipeGotMaterial(recipe, isEnabled() ? Material.GOLD_NUGGET : Material.GOLD_INGOT)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
