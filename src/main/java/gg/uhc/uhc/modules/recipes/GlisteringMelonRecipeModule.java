/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.recipes.GlisteringMelonRecipeModule
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
        setId("GlisteringMelonRecipe");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.SPECKLED_MELON);
        this.icon.setWeight(ModuleRegistry.CATEGORY_RECIPIES);

        ShapelessRecipe modified = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1))
                .addIngredient(1, Material.GOLD_BLOCK)
                .addIngredient(1, Material.MELON);

        Bukkit.addRecipe(modified);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
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
