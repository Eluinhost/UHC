package gg.uhc.uhc.modules.inventory;

import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.uhc.PlayerResetter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ClearXPCommand extends OptionCommand {

    protected final OptionSpec<Player> playersSpec;

    protected final PlayerResetter resetter;

    public ClearXPCommand(PlayerResetter resetter) {
        this.resetter = resetter;

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
            resetter.resetExp(player);
            player.sendMessage(ChatColor.AQUA + "Your XP was reset");
        }

        sender.sendMessage(ChatColor.AQUA + "Player XP reset");
        return true;
    }
}
