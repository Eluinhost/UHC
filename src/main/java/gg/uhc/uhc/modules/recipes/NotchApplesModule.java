/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.recipes.NotchApplesModule
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

import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class NotchApplesModule extends DisableableModule implements Listener {

    protected static final short NOTCH_APPLE_DURABILITY = 1;
    protected static final ItemStack NOTCH_APPLE_ITEM = new ItemStack(Material.GOLDEN_APPLE, 1, NOTCH_APPLE_DURABILITY);

    protected static final ShapedRecipe NOTCH_APPLE_RECIPE = new ShapedRecipe(NOTCH_APPLE_ITEM)
            .shape("GGG", "GAG", "GGG")
            .setIngredient('G', Material.GOLD_BLOCK)
            .setIngredient('A', Material.APPLE);

    protected static final Predicate<Recipe> IS_NOTCH_APPLE_RECIPE = new Predicate<Recipe>() {
        @Override
        public boolean apply(Recipe recipe) {
            return recipe.getResult().equals(NOTCH_APPLE_ITEM);
        }
    };

    protected static final String ICON_NAME = "Notch apples";

    public NotchApplesModule() {
        setId("NotchApples");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.GOLDEN_APPLE);
        this.icon.setDurability(NOTCH_APPLE_DURABILITY);
        this.icon.setWeight(ModuleRegistry.CATEGORY_RECIPIES);
    }

    @Override
    protected void onEnable() {
        final boolean recipeExists = Iterators.any(Bukkit.recipeIterator(), IS_NOTCH_APPLE_RECIPE);
        if (!recipeExists) Bukkit.addRecipe(NOTCH_APPLE_RECIPE);
    }

    @Override
    protected void onDisable() {
        Iterators.removeIf(Bukkit.recipeIterator(), IS_NOTCH_APPLE_RECIPE);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }
}
