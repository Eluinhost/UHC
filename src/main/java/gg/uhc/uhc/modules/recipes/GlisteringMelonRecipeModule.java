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
import org.bukkit.inventory.ShapelessRecipe;

public class GlisteringMelonRecipeModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Glistering Melon Recipe";

    public GlisteringMelonRecipeModule(IconStack icon, boolean enabled) {
        super(ICON_NAME, icon, enabled);

        // TODO allow permission?

        icon.setType(Material.SPECKLED_MELON);

        ShapelessRecipe modified = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1))
                .addIngredient(1, Material.GOLD_BLOCK)
                .addIngredient(1, Material.MELON);

        Bukkit.addRecipe(modified);
    }

    @Override
    public void onEnable() {
        setLore("Requires a golden block to craft");
    }

    @Override
    public void onDisable() {
        setLore("Requires 8 golden nuggets to craft");
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.SPECKLED_MELON) return;

        if (RecipeUtil.hasRecipeGotMaterial(recipe, enabled ? Material.GOLD_NUGGET : Material.GOLD_BLOCK)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
