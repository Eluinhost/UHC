/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.potions.WitchesModule
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

package gg.uhc.uhc.modules.potions;

import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class WitchesModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Witch spawns";

    public WitchesModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.FLOWER_POT_ITEM);
        this.icon.setWeight(ModuleRegistry.CATEGORY_MISC);
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Witches can spawn");
        } else {
            icon.setLore("Witches cannot spawn");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(CreatureSpawnEvent event) {
        if (isEnabled()) return;

        if (event.getEntity().getType() == EntityType.WITCH)
            event.setCancelled(true);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
