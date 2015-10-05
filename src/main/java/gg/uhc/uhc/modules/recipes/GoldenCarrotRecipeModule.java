package gg.uhc.uhc.modules.recipes;

import gg.uhc.uhc.inventory.IconStack;
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

    public GoldenCarrotRecipeModule(IconStack icon, boolean enabled) {
        super(ICON_NAME, icon, enabled);

        // TODO allow permission?

        icon.setType(Material.GOLDEN_CARROT);

        // register the new recipe
        ShapedRecipe modified = new ShapedRecipe(new ItemStack(Material.GOLDEN_CARROT, 1))
                .shape("AAA", "ABA", "AAA")
                .setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.CARROT_ITEM);

        Bukkit.addRecipe(modified);
    }

    @Override
    public void onEnable() {
        icon.setLore("Requires golden ingots to craft");
    }

    @Override
    public void onDisable() {
        icon.setLore("Requires golden nuggets to craft");
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.GOLDEN_CARROT) return;

        if (RecipeUtil.hasRecipeGotMaterial(recipe, enabled ? Material.GOLD_NUGGET : Material.GOLD_INGOT)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
