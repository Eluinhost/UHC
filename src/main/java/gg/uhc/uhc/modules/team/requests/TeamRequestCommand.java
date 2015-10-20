package gg.uhc.uhc.modules.team.requests;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.StringConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.StringPredicates;
import gg.uhc.uhc.modules.team.FunctionalUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TeamRequestCommand extends OptionCommand {

    protected final RequestManager requests;

    protected OptionSpec<String> playerNameSpec;

    public TeamRequestCommand(RequestManager requests) {
        this.requests = requests;

        playerNameSpec = parser.nonOptions("Player names to create a team with")
                .withValuesConvertedBy(new StringConverter().setPredicate(new StringPredicates.LessThanOrEqualLength(16)).setType("Player name"));
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You may only use this command as a player. Use the teamup command to create teams for other people");
            return true;
        }

        TeamRequest request = new TeamRequest(((Player) sender).getUniqueId(), ImmutableSet.copyOf(playerNameSpec.values(options)));
        requests.addRequest(request);

        return true;
    }

    @Override
    protected List<String> runTabComplete(CommandSender sender, String[] args) {
        Set<String> players = Sets.newHashSet(Iterables.transform(Bukkit.getOnlinePlayers(), FunctionalUtil.PLAYER_NAME_FETCHER));

        // remove sender's name
        players.remove(sender.getName());

        // remove all the names already provided
        players.removeAll(Arrays.asList(args));

        return StringUtil.copyPartialMatches(args[args.length - 1], players, Lists.<String>newArrayList());
    }
}
