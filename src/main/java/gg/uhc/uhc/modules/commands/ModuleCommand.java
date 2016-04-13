/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.commands.ModuleCommand
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

import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.ModuleRegistry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class ModuleCommand extends TemplatedOptionCommand {

    public enum Type {
        ENABLE,
        DISABLE,
        TOGGLE
    }

    protected static final Function<Module, String> FETCH_KEY_AS_STRING = new Function<Module, String>() {
        @Override
        public String apply(Module input) {
            return input.getId();
        }
    };

    protected final ModuleRegistry registry;
    protected final Type type;
    protected final OptionSpec<Map.Entry<String, Module>> moduleSpec;

    public ModuleCommand(MessageTemplates messages, ModuleRegistry registry, Type type) {
        super(messages);

        this.registry = registry;
        this.type = type;

        moduleSpec = parser
                .nonOptions("List of module ids to " + type.name().toLowerCase())
                .withValuesConvertedBy(new ModuleEntryConverter(registry));
        nonOptionsTabComplete = new NonDuplicateTabComplete(new ModuleTabComplete(registry));
    }

    @Override
    protected final boolean runCommand(CommandSender sender, OptionSet options) {
        final List<Map.Entry<String, Module>> entries = moduleSpec.values(options);

        if (entries.size() == 0) {
            sender.sendMessage(messages.evalTemplate(
                    "provide modules",
                    ImmutableMap.of(
                            "modules",
                            Joiner.on(", ").join(Iterables.transform(registry.getModules(), FETCH_KEY_AS_STRING))
                    )
            ));
            return true;
        }

        final List<String> newStates = Lists.newArrayListWithCapacity(entries.size());
        int count = 0;
        for (final Map.Entry<String, Module> entry : entries) {
            final Module module = entry.getValue();
            final String id = entry.getKey();

            final ChatColor stateColour;

            if (module instanceof DisableableModule) {
                final DisableableModule disableable = (DisableableModule) module;

                switch (type) {
                    case ENABLE:
                        if (!disableable.isEnabled() && disableable.enable()) {
                            disableable.announceState();
                            count++;
                        }
                        break;
                    case DISABLE:
                        if (disableable.isEnabled() && disableable.disable()) {
                            disableable.announceState();
                            count++;
                        }
                        break;
                    case TOGGLE:
                        if (disableable.toggle()) {
                            disableable.announceState();
                            count++;
                        }
                        break;
                    default:
                }

                stateColour = disableable.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
            } else {
                stateColour = ChatColor.GRAY;
            }

            newStates.add(stateColour + id);
        }

        sender.sendMessage(messages.evalTemplate(
                "processed",
                ImmutableMap.of(
                        "completed", count,
                        "total", entries.size(),
                        "states", Joiner.on(", ").join(newStates)
                )
        ));
        return true;
    }
}
