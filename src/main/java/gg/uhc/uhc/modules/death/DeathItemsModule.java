/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.death.DeathItemsModule
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

package gg.uhc.uhc.modules.death;

import gg.uhc.flagcommands.converters.EnumConverter;
import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathItemsModule extends DisableableModule implements Listener {

    protected static final String ITEMS_KEY = "items";
    protected static final String TYPE_KEY = "type";
    protected static final String AMOUNT_KEY = "amount";
    protected static final String DATA_KEY = "data";

    protected static final int DEFAULT_ITEM_AMOUNT_1 = 2;
    protected static final int DEFAULT_ITEM_DATA_1 = 4;
    protected static final int DEFAULT_ITEM_AMOUNT_2 = 3;

    protected static final String ICON_NAME = "Death Items";

    protected List<ItemStack> stacks = Lists.newArrayList();

    public DeathItemsModule() {
        setId("DeathItems");

        this.iconName = ICON_NAME;
        this.icon.setType(Material.BUCKET);
        this.icon.setWeight(ModuleRegistry.CATEGORY_DEATH);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains(ITEMS_KEY)) {
            final ConfigurationSection section = config.createSection(ITEMS_KEY);

            final ConfigurationSection wool = section.createSection("example wool");
            wool.set(TYPE_KEY, "WOOL");
            wool.set(AMOUNT_KEY, DEFAULT_ITEM_AMOUNT_1);
            wool.set(DATA_KEY, DEFAULT_ITEM_DATA_1);

            final ConfigurationSection dirt = section.createSection("example dirt");
            dirt.set(TYPE_KEY, "DIRT");
            dirt.set(AMOUNT_KEY, DEFAULT_ITEM_AMOUNT_2);
        }

        final ConfigurationSection itemsSection = config.getConfigurationSection(ITEMS_KEY);

        for (final String key : itemsSection.getKeys(false)) {
            final ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);

            if (!itemSection.contains(AMOUNT_KEY)) {
                throw new InvalidConfigurationException("A drop item requires an `amount`");
            }

            if (!itemSection.contains(TYPE_KEY)) {
                throw new InvalidConfigurationException("A drop item requires a `type`");
            }

            final int amount = itemSection.getInt(AMOUNT_KEY);

            if (amount <= 0) throw new InvalidConfigurationException("A drop item cannot have a <= 0 amount");

            final Material type;
            try {
                type = EnumConverter.forEnum(Material.class).convert(itemSection.getString(TYPE_KEY));
            } catch (ValueConversionException ex) {
                throw new InvalidConfigurationException(
                        "Invalid material for drop item: " + itemSection.getString(TYPE_KEY),
                        ex
                );
            }

            final ItemStack stack = new ItemStack(type, amount);

            if (itemSection.contains(DATA_KEY)) {
                stack.setDurability((short) itemSection.getInt(DATA_KEY));
            }

            stacks.add(stack);
        }

        super.initialize();
    }

    @Override
    protected List<String> getEnabledLore() {
        final List<String> lore = Lists.newArrayList();

        lore.addAll(messages.getRawStrings(ENABLED_LORE_PATH + ".header"));

        for (final ItemStack stack : stacks) {
            lore.addAll(messages.evalTemplates(ENABLED_LORE_PATH + ".stack", new StackContext(stack)));
        }

        return lore;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        event.getDrops().addAll(stacks);
    }


    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    protected static class StackContext {
        protected final ItemStack stack;

        public StackContext(ItemStack stack) {
            this.stack = stack;
        }

        public String amount() {
            return String.valueOf(stack.getAmount());
        }

        public String type() {
            return stack.getType().name();
        }

        public String data() {
            if (stack.getDurability() == 0) {
                return "";
            }

            return ":" + stack.getDurability();
        }
    }
}
