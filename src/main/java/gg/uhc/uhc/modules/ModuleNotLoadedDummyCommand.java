package gg.uhc.uhc.modules;

import gg.uhc.uhc.command.StaticStringCommand;
import org.bukkit.ChatColor;

public class ModuleNotLoadedDummyCommand extends StaticStringCommand {
    public ModuleNotLoadedDummyCommand(String moduleName) {
        super(ChatColor.RED + "This command is not runnable because the module `" + moduleName + "` was chosen not to load in the configuration.");
    }
}
