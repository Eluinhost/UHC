package gg.uhc.uhc.modules.team;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gg.uhc.uhc.command.OptionCommand;
import gg.uhc.uhc.command.converters.IntegerConverter;
import gg.uhc.uhc.command.converters.selection.SelectionPredicate;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class ListTeamsCommand extends OptionCommand {

    protected static final String HEADER_SINGLE_PAGE = ChatColor.AQUA + "Showing %d results %s";
    protected static final String HEADER_MULTIPLE_PAGE = HEADER_SINGLE_PAGE + " p%d/%d %d total. Use `-p` to view other pages";
    protected static final String NO_MEMBERS = ChatColor.DARK_GRAY + "No members";
    protected static final String FORMAT = "%s" + ChatColor.LIGHT_PURPLE + " - (%s): " + ChatColor.DARK_PURPLE + "%s";
    protected static final int COUNT_PER_PAGE = 16;

    protected final TeamModule teamModule;

    protected final OptionSpec<Void> showAllSpec;
    protected final OptionSpec<Void> emptyOnlySpec;
    protected final OptionSpec<Integer> pageSpec;

    public ListTeamsCommand(TeamModule teamModule) {
        this.teamModule = teamModule;

        showAllSpec = parser
                .acceptsAll(ImmutableList.of("a", "all"), "Show all teams");

        emptyOnlySpec = parser
                .acceptsAll(ImmutableList.of("e", "empty"), "Show empty teams only");

        pageSpec = parser
                .acceptsAll(ImmutableList.of("p", "page"), "The page of results to show")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter(SelectionPredicate.POSITIVE_INTEGER))
                .defaultsTo(1);
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        int page = pageSpec.value(options);
        boolean emptyOnly = options.has(emptyOnlySpec);
        boolean showAll = options.has(showAllSpec);

        if (showAll && emptyOnly){
            sender.sendMessage(ChatColor.RED + "You must provide -e OR -a, you cannot supply both");
            return true;
        }

        Predicate<Team> predicate;
        String type;

        if (emptyOnly) {
            type = "(empty teams)";
            predicate = Predicates.not(WITH_PLAYERS);
        } else if (showAll) {
            type = "(all teams)";
            predicate = Predicates.alwaysTrue();
        } else {
            type = "(with players)";
            predicate = WITH_PLAYERS;
        }

        List<Team> teams = Lists.newArrayList(Iterables.filter(teamModule.getTeams().values(), predicate));

        if (teams.size() == 0) {
            sender.sendMessage(ChatColor.RED + "No results found for query " + type);
            return true;
        }

        List<List<Team>> partitioned = Lists.partition(teams, COUNT_PER_PAGE);

        if (page > partitioned.size()) {
            sender.sendMessage(ChatColor.RED + "Page " + page + " does not exist");
            return true;
        }

        List<Team> pageItems = partitioned.get(page - 1);

        if (partitioned.size() == 1) {
            sender.sendMessage(String.format(HEADER_SINGLE_PAGE, pageItems.size(), type));
        } else {
            sender.sendMessage(String.format(HEADER_MULTIPLE_PAGE, pageItems.size(), type, page, partitioned.size(), teams.size()));
        }

        Joiner joiner = Joiner.on(", ");
        for (Team team : pageItems) {
            String memberString;
            Set<OfflinePlayer> members = team.getPlayers();

            if (members.size() == 0) {
                memberString = NO_MEMBERS;
            } else {
                memberString = joiner.join(Iterables.transform(team.getPlayers(), NAME_FETCHER));
            }

            sender.sendMessage(String.format(FORMAT, team.getPrefix() + team.getDisplayName(), team.getName(), memberString));
        }

        return true;
    }

    protected static final Predicate<Team> WITH_PLAYERS = new Predicate<Team>() {
        @Override
        public boolean apply(@Nullable Team input) {
            assert input != null;
            return input.getPlayers().size() > 0;
        }
    };

    protected static final Function<OfflinePlayer, String> NAME_FETCHER = new Function<OfflinePlayer, String>() {
        @Nullable
        @Override
        public String apply(OfflinePlayer input) {
            return input.getName();
        }
    };
}
