package gg.uhc.uhc.modules.commands;

import gg.uhc.uhc.command.SubcommandCommand;
import gg.uhc.uhc.modules.ModuleRegistry;

public class ModuleCommands extends SubcommandCommand {

    protected final ModuleRegistry registry;
    protected final ModuleEntryConverter moduleEntryConverter;

    public ModuleCommands(ModuleRegistry registry) {
        this.registry = registry;
        this.moduleEntryConverter = new ModuleEntryConverter(registry);

        registerSubcommand("toggle", new ModuleCommand(registry, ModuleCommand.Type.TOGGLE));
        registerSubcommand("enable", new ModuleCommand(registry, ModuleCommand.Type.ENABLE));
        registerSubcommand("disable", new ModuleCommand(registry, ModuleCommand.Type.DISABLE));
    }
}
