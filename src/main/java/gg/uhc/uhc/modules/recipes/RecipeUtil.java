package gg.uhc.uhc.modules.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Collection;

public class RecipeUtil {
    /**
     * Check if the recipe has the given material in it
     *
     * @param r   the recipe to check
     * @param mat the material to look for
     * @return true if found, false if not
     */
    public static boolean hasRecipeGotMaterial(Recipe r, Material mat) {
        Collection<ItemStack> ingredients = null;

        if(r instanceof ShapedRecipe) {
            ingredients = ((ShapedRecipe) r).getIngredientMap().values();
        } else if(r instanceof ShapelessRecipe) {
            ingredients = ((ShapelessRecipe) r).getIngredientList();
        }

        if (null == ingredients) return false;

        for (ItemStack stack : ingredients) {
            if (stack.getType() == mat) return true;
        }

        return false;
    }
}
