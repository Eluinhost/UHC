package gg.uhc.uhc.modules.food;

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
