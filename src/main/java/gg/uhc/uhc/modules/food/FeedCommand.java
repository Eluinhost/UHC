package gg.uhc.uhc.modules.food;

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

public class FeedCommand extends OptionCommand {

    protected final OptionSpec<Player> playersSpec;

    protected final PlayerResetter resetter;

    public FeedCommand(PlayerResetter resetter) {
        this.resetter = resetter;

        playersSpec = parser.nonOptions("List of online players to feed, leave empty to feed all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> toFeed = playersSpec.values(options);

        if (toFeed.size() == 0) {
            toFeed = Bukkit.getOnlinePlayers();
        }

        for (Player player : toFeed) {
            resetter.resetFood(player);
            player.sendMessage(ChatColor.AQUA + "You were fed back to full hunger");
        }

        sender.sendMessage(ChatColor.AQUA + "Players fed");
        return true;
    }
}
