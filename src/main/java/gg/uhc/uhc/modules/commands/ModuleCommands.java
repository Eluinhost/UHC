/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.commands.ModuleCommands
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

import com.google.common.collect.ImmutableSet;
import gg.uhc.flagcommands.commands.SubcommandCommand;
import gg.uhc.uhc.inventory.ShowIconsCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import gg.uhc.uhc.modules.ModuleRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class ModuleCommands extends SubcommandCommand {

    protected final ModuleRegistry registry;
    protected final ModuleEntryConverter moduleEntryConverter;
    protected final MessageTemplates messages;

    protected final Set<String> noPerm = ImmutableSet.of("show", "status");

    public ModuleCommands(MessageTemplates messages, ModuleRegistry registry) {
        this.messages = messages;
        this.registry = registry;
        this.moduleEntryConverter = new ModuleEntryConverter(registry);

        registerSubcommand("toggle", new ModuleCommand(messages, registry, ModuleCommand.Type.TOGGLE));
        registerSubcommand("enable", new ModuleCommand(messages, registry, ModuleCommand.Type.ENABLE));
        registerSubcommand("disable", new ModuleCommand(messages, registry, ModuleCommand.Type.DISABLE));
        registerSubcommand("status", new ModuleStatusCommand(registry));

        ShowIconsCommand icons = new ShowIconsCommand(messages, registry.getInventory());
        registerSubcommand("show", icons);
        registerSubcommand(NO_ARG_SPECIAL, icons);
    }

    // add a extra permission check for any subcommands other than show or no-arg
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // allow noarg + noPerm commands to bypass perm check
        if (args.length != 0 && !noPerm.contains(args[0].toLowerCase()) && !sender.hasPermission("uhc.command.uhc.admin")) {
            sender.sendMessage(messages.getRaw("no modify permission"));
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }
}
