/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.ListTeamsCommand
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.modules.team;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.converters.IntegerConverter;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.IntegerPredicates;
import gg.uhc.flagcommands.tab.OptionsTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListTeamsCommand extends TemplatedOptionCommand {

    protected static final String NO_MEMBERS = ChatColor.DARK_GRAY + "No members";
    protected static final String FORMAT = "%s" + ChatColor.LIGHT_PURPLE + " - (%s): " + ChatColor.DARK_PURPLE + "%s";
    protected static final int COUNT_PER_PAGE = 16;

    protected final TeamModule teamModule;

    protected final OptionSpec<Void> showAllSpec;
    protected final OptionSpec<Void> emptyOnlySpec;
    protected final ArgumentAcceptingOptionSpec<Integer> pageSpec;

    public ListTeamsCommand(MessageTemplates messages, TeamModule teamModule) {
        super(messages);
        this.teamModule = teamModule;

        showAllSpec = parser
                .acceptsAll(ImmutableList.of("a", "all"), "Show all teams");

        emptyOnlySpec = parser
                .acceptsAll(ImmutableList.of("e", "empty"), "Show empty teams only");

        pageSpec = parser
                .acceptsAll(ImmutableList.of("p", "page"), "The page of results to show")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter().setPredicate(IntegerPredicates.GREATER_THAN_ZERO).setType("Integer > 0"))
                .defaultsTo(1);

        completers.put(pageSpec, new OptionsTabComplete() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, String complete, String[] others) {
                List<String> results = Lists.newArrayList();

                int pages = (int) Math.ceil((double) ListTeamsCommand.this.teamModule.getTeams().size() / (double) COUNT_PER_PAGE);

                for (int i = 1; i <= pages; i++) {
                    results.add(String.valueOf(i));
                }

                return StringUtil.copyPartialMatches(complete, results, Lists.<String>newArrayList());
            }
        });
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
            predicate = Predicates.not(FunctionalUtil.TEAMS_WITH_PLAYERS);
        } else if (showAll) {
            type = "(all teams)";
            predicate = Predicates.alwaysTrue();
        } else {
            type = "(with players)";
            predicate = FunctionalUtil.TEAMS_WITH_PLAYERS;
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

        Map<String, Object> context = ImmutableMap.<String, Object>builder()
                .put("page", page)
                .put("pages", partitioned.size())
                .put("type", type)
                .put("count", pageItems.size())
                .put("teams", teams.size())
                .put("multiple", partitioned.size() > 1)
                .build();

        sender.sendMessage(messages.evalTemplate("header", context));

        Joiner joiner = Joiner.on(", ");
        for (Team team : pageItems) {
            String memberString;
            Set<OfflinePlayer> members = team.getPlayers();

            if (members.size() == 0) {
                memberString = NO_MEMBERS;
            } else {
                memberString = joiner.join(Iterables.transform(team.getPlayers(), FunctionalUtil.PLAYER_NAME_FETCHER));
            }

            sender.sendMessage(String.format(FORMAT, team.getPrefix() + team.getDisplayName(), team.getName(), memberString));
        }

        return true;
    }
}
