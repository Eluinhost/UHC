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

public class ResetPlayerCommand extends OptionCommand {

    protected final OptionSpec<Player> playersSpec;

    protected final PlayerResetter resetter;

    public ResetPlayerCommand(PlayerResetter resetter) {
        this.resetter = resetter;

        playersSpec = parser.nonOptions("List of online players to reset, leave empty for all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> toClear = playersSpec.values(options);

        if (toClear.size() == 0) {
            toClear = Bukkit.getOnlinePlayers();
        }

        for (Player player : toClear) {
            resetter.reset(player);
            player.sendMessage(ChatColor.AQUA + "You were reset");
        }

        sender.sendMessage(ChatColor.AQUA + "Players reset");
        return true;
    }
}
