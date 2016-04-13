/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.recipes.RecipeUtil
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.modules.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Collection;

public final class RecipeUtil {

    private RecipeUtil() {}

    /**
     * Check if the recipe has the given material in it.
     *
     * @param recipe   the recipe to check
     * @param mat the material to look for
     * @return true if found, false if not
     */
    public static boolean hasRecipeGotMaterial(Recipe recipe, Material mat) {
        Collection<ItemStack> ingredients = null;

        if (recipe instanceof ShapedRecipe) {
            ingredients = ((ShapedRecipe) recipe).getIngredientMap().values();
        } else if (recipe instanceof ShapelessRecipe) {
            ingredients = ((ShapelessRecipe) recipe).getIngredientList();
        }

        if (null == ingredients) return false;

        for (final ItemStack stack : ingredients) {
            if (stack.getType() == mat) return true;
        }

        return false;
    }
}
