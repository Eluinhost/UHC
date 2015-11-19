/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.heads.HeadDropsModule
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

package gg.uhc.uhc.modules.heads;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.death.StandItemsMetadata;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.Random;

public class HeadDropsModule extends DisableableModule implements Listener {

    protected static final Random random = new Random();
    protected static final String ICON_NAME = "Head Drops";

    protected static final NumberFormat formatter = NumberFormat.getNumberInstance();

    static {
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(1);
    }

    protected final PlayerHeadProvider playerHeadProvider;
    protected double dropRate = 0;

    public HeadDropsModule(PlayerHeadProvider playerHeadProvider) {
        this.playerHeadProvider = playerHeadProvider;
        this.iconName = ICON_NAME;
        this.icon.setType(Material.SKULL_ITEM);
        this.icon.setDurability((short) 3);
        this.icon.setWeight(ModuleRegistry.CATEGORY_APPLES);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains("drop chance")) {
            config.set("drop chance", 100D);
        }

        if (!config.isDouble("drop chance") && !config.isInt("drop chance"))
            throw new InvalidConfigurationException("Invalid value at " + config.getCurrentPath() + ".drop chance (" + config.get("drop chance"));

        dropRate = config.getDouble("drop chance") / 100D;

        super.initialize();
    }

    public double getDropRate() {
        return dropRate;
    }

    public void setDropRate(double rate) {
        Preconditions.checkArgument(rate >= 0D && rate <= 1D);
        this.dropRate = rate;
        config.set("drop chance", this.dropRate);
        saveConfig();
        rerender();
    }

    @Override
    protected void rerender() {
        super.rerender();

        icon.setLore(isEnabled() ? messages.evalTemplate("enabled lore", ImmutableMap.of("rate", formatter.format(dropRate * 100))) : messages.getRaw("disabled lore"));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        if (!isEnabled() || random.nextDouble() < (1D - dropRate)) {
            // set to an empty map to avoid stale metadata problems
            event.getEntity().setMetadata(StandItemsMetadata.KEY, new StandItemsMetadata(plugin));
            return;
        }

        Player player = event.getEntity();

        // create a head
        ItemStack head = playerHeadProvider.getPlayerHeadItem(player);

        // add it to the drops
        event.getDrops().add(head);

        // add metadata for the armour stand module to put the helmet on the player and remove from drops
        EnumMap<EquipmentSlot, ItemStack> standItems = Maps.newEnumMap(EquipmentSlot.class);
        standItems.put(EquipmentSlot.HEAD, head);

        player.setMetadata(StandItemsMetadata.KEY, new StandItemsMetadata(plugin, standItems));
    }
}
