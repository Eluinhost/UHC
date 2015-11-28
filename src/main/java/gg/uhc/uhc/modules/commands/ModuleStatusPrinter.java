/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.commands.ModuleStatusPrinter
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

package gg.uhc.uhc.modules.commands;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import gg.uhc.uhc.ItemStackNBTStringFetcher;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.Module;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModuleStatusPrinter {

    protected static final Function<Module, String> SIMPLE = new Function<Module, String>() {
        @Override
        public String apply(Module input) {
            return colourForModule(input) + input.getId();
        }
    };

    protected static ChatColor colourForModule(Module module) {
        if (module instanceof DisableableModule) {
            return ((DisableableModule) module).isEnabled() ? ChatColor.GREEN : ChatColor.RED;
        }

        return ChatColor.DARK_GRAY;
    }

    protected static final Function<Module, TextComponent> COMPLEX = new Function<Module, TextComponent>() {
        @Override
        public TextComponent apply(Module input) {
            TextComponent component = new TextComponent(input.getId());

            component.setColor(colourForModule(input));

            TextComponent itemNBT = new TextComponent(ItemStackNBTStringFetcher.readFromItemStack(input.getIconStack()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{ itemNBT }));

            return component;
        }
    };

    protected static final Comparator<Module> BY_ID = new Comparator<Module>() {
        @Override
        public int compare(Module o1, Module o2) {
            return Ordering.natural().compare(o1.getId(), o2.getId());
        }
    };

    public String simple(Module module) {
        return SIMPLE.apply(module);
    }

    public TextComponent complex(Module module) {
        return COMPLEX.apply(module);
    }

    public String simple(List<Module> modules, String separator) {
        Collections.sort(modules, BY_ID);

        return Joiner.on(separator).join(Iterables.transform(modules, SIMPLE));
    }

    public TextComponent complex(List<Module> modules, String separator) {
        Collections.sort(modules, BY_ID);

        TextComponent parent = new TextComponent("");

        List<TextComponent> toAdd = Lists.transform(modules, COMPLEX);

        int finalIndex = toAdd.size() - 1;
        for (int i = 0; i < finalIndex + 1; i++) {
            parent.addExtra(toAdd.get(i));

            if (i != finalIndex) {
                parent.addExtra(new TextComponent(separator));
            }
        }

        return parent;
    }
}
