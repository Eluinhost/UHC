/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.potions.Tier2PotionsModule
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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Tier2PotionsModule extends DisableableModule {

    protected static final String ICON_NAME = "Tier 2 Potion Brewing";

    protected final PotionFuelsListener listener;

    public Tier2PotionsModule(PotionFuelsListener listener) {
        this.listener = listener;

        Potion potion = new Potion(PotionType.INSTANT_HEAL);
        potion.setLevel(2);

        this.icon.setType(Material.POTION);
        this.icon.setDurability(potion.toDamageValue());
        this.iconName = ICON_NAME;
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Tier 2 potions are brewable");
        } else {
            icon.setLore("Disabled tier 2 potion brewing");
        }
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public void onEnable() {
        listener.removeMaterial(Material.GLOWSTONE_DUST);
    }

    @Override
    public void onDisable() {
        listener.addMaterial(Material.GLOWSTONE_DUST, ChatColor.RED + "Tier 2 potions are unbrewable.");
    }
}
