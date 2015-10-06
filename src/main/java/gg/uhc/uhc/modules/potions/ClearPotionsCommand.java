package gg.uhc.uhc.modules.potions;

import gg.uhc.uhc.PlayerResetter;
import gg.uhc.uhc.command.OptionCommand;
import gg.uhc.uhc.command.converters.OnlinePlayerConverter;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ClearPotionsCommand extends OptionCommand {

    protected final OptionSpec<Player> playersSpec;

    protected final PlayerResetter resetter;

    public ClearPotionsCommand(PlayerResetter resetter) {
        this.resetter = resetter;

        playersSpec = parser.nonOptions("List of online players to clear potion effects on, leave empty for all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> toClear = playersSpec.values(options);

        if (toClear.size() == 0) {
            toClear = Bukkit.getOnlinePlayers();
        }

        for (Player player : toClear) {
            resetter.resetEffects(player);
            player.sendMessage(ChatColor.AQUA + "Your potion effects were reset");
        }

        sender.sendMessage(ChatColor.AQUA + "Player potion effects reset");
        return true;
    }
}
