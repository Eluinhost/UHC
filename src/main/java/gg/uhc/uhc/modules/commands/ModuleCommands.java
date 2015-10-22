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

import gg.uhc.flagcommands.commands.SubcommandCommand;
import gg.uhc.uhc.inventory.ShowIconsCommand;
import gg.uhc.uhc.modules.ModuleRegistry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ModuleCommands extends SubcommandCommand {

    protected final ModuleRegistry registry;
    protected final ModuleEntryConverter moduleEntryConverter;
    protected final ShowIconsCommand iconsCommand;

    public ModuleCommands(ModuleRegistry registry, ShowIconsCommand iconsCommand) {
        this.registry = registry;
        this.iconsCommand = iconsCommand;
        this.moduleEntryConverter = new ModuleEntryConverter(registry);

        registerSubcommand("toggle", new ModuleCommand(registry, ModuleCommand.Type.TOGGLE));
        registerSubcommand("enable", new ModuleCommand(registry, ModuleCommand.Type.ENABLE));
        registerSubcommand("disable", new ModuleCommand(registry, ModuleCommand.Type.DISABLE));
        registerSubcommand("show", iconsCommand);
    }

    // override logic to make inventory open on no-arg instead of saying usage
    // also adds an extra permission check for any subcommands
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            return iconsCommand.onCommand(sender, command, label, args);
        }

        if (!sender.hasPermission("uhc.command.uhc.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to modify modules. Run the command without any arguments to open the config viewer");
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }
}
