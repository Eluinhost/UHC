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
