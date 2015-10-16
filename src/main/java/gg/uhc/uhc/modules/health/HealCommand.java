package gg.uhc.uhc.modules.health;

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
