package gg.uhc.uhc.modules.inventory;

import gg.uhc.uhc.command.OptionCommand;
import gg.uhc.uhc.command.converters.OnlinePlayerConverter;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ClearXPCommand extends OptionCommand {

    protected final OptionSpec<Player> playersSpec;

    public ClearXPCommand() {
        playersSpec = parser.nonOptions("List of online players to clear XP for, leave empty for all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> toClear = playersSpec.values(options);

        if (toClear.size() == 0) {
            toClear = Bukkit.getOnlinePlayers();
        }

        for (Player player : toClear) {
            player.setExp(0);
            player.setLevel(0);
            player.setTotalExperience(0);
            player.sendMessage(ChatColor.AQUA + "Your XP was reset");
        }

        sender.sendMessage(ChatColor.AQUA + "Player XP reset");
        return true;
    }
}
