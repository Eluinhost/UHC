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

import com.google.common.collect.Lists;
import gg.uhc.flagcommands.converters.EnumConverter;
import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
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

    protected static final String ICON_NAME = "Death Items";

    protected List<ItemStack> stacks = Lists.newArrayList();

    public DeathItemsModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.BUCKET);
        this.icon.setWeight(ModuleRegistry.CATEGORY_DEATH);
    }

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains("items")) {
            ConfigurationSection s = config.createSection("items");
            ConfigurationSection wool = s.createSection("example wool");
            ConfigurationSection dirt = s.createSection("example dirt");

            wool.set("type", "WOOL");
            wool.set("amount", 2);
            wool.set("data", 4);

            dirt.set("type", "DIRT");
            dirt.set("amount", 3);
        }
        ConfigurationSection itemsSection = config.getConfigurationSection("items");

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);

            if (!itemSection.contains("amount")) throw new InvalidConfigurationException("A drop item requires an `amount`");
            if (!itemSection.contains("type")) throw new InvalidConfigurationException("A drop item requires a `type`");

            int amount = itemSection.getInt("amount");

            if (amount <= 0) throw new InvalidConfigurationException("A drop item cannot have a <= 0 amount");

            Material type;
            try {
                type = EnumConverter.forEnum(Material.class).convert(itemSection.getString("type"));
            } catch (ValueConversionException ex) {
                throw new InvalidConfigurationException("Invalid material for drop item: " + itemSection.getString("type"), ex);
            }

            ItemStack stack = new ItemStack(type, amount);

            if (itemSection.contains("data")) {
                stack.setDurability((short) itemSection.getInt("data"));
            }

            stacks.add(stack);
        }

        super.initialize();
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            List<String> lore = Lists.newArrayListWithCapacity(1 + stacks.size());
            lore.add(messages.getRaw("enabled lore.header"));

            for (ItemStack stack : stacks) {
                lore.add(messages.evalTemplate("enabled lore.stack", new StackContext(stack)));
            }

            icon.setLore(lore.toArray(new String[lore.size()]));
        } else {
            icon.setLore(messages.getRaw("disabled lore"));
        }
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
