package gg.uhc.uhc.modules.health;

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

public class HealCommand extends OptionCommand {

    protected final OptionSpec<Player> playersSpec;

    protected final PlayerResetter resetter;

    public HealCommand(PlayerResetter resetter) {
        this.resetter = resetter;

        playersSpec = parser.nonOptions("List of online players to heal, leave empty to heal all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> toHeal = playersSpec.values(options);

        if (toHeal.size() == 0) {
            toHeal = Bukkit.getOnlinePlayers();
        }

        for (Player player : toHeal) {
            resetter.resetHealth(player);
            player.sendMessage(ChatColor.AQUA + "You were healed back to full health");
        }

        sender.sendMessage(ChatColor.AQUA + "Players healed");
        return true;
    }
}
