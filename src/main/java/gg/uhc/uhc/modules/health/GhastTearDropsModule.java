/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.health.GhastTearDropsModule
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

package gg.uhc.uhc.modules.health;

import com.google.common.collect.ImmutableMap;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GhastTearDropsModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Ghast Tears";

    public GhastTearDropsModule() {
        setId("GhastTears");

        this.iconName = ICON_NAME;
        this.icon.setWeight(ModuleRegistry.CATEGORY_POTIONS);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    protected void renderEnabled() {
        super.renderEnabled();
        icon.setType(Material.GHAST_TEAR);
    }

    @Override
    protected void renderDisabled() {
        super.renderDisabled();
        icon.setType(Material.GOLD_INGOT);
    }

    @Override
    protected List<String> getEnabledLore() {
        return messages.evalTemplates("lore", ImmutableMap.of("item", "ghast tears"));
    }

    @Override
    protected List<String> getDisabledLore() {
        return messages.evalTemplates("lore", ImmutableMap.of("item", "gold ingots"));
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (isEnabled() || event.getEntity().getType() != EntityType.GHAST) return;

        for (ItemStack drop : event.getDrops()) {
            if (drop.getType() == Material.GHAST_TEAR) {
                drop.setType(Material.GOLD_INGOT);
            }
        }
    }
}
