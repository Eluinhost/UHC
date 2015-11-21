/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.RandomTeamsCommand
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
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import gg.uhc.flagcommands.converters.IntegerConverter;
import gg.uhc.flagcommands.converters.OfflinePlayerConverter;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.IntegerPredicates;
import gg.uhc.flagcommands.tab.FixedValuesTabComplete;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RandomTeamsCommand extends TemplatedOptionCommand {

    protected final TeamModule module;

    protected final OptionSpec<Player> playersSpec;
    protected final ArgumentAcceptingOptionSpec<Integer> teamCountSpec;
    protected final ArgumentAcceptingOptionSpec<Integer> teamSizeSpec;
    protected final ArgumentAcceptingOptionSpec<OfflinePlayer> excludingSpec;
    protected final OptionSpec<Void> excludeExtrasSpec;

    public RandomTeamsCommand(MessageTemplates messages, TeamModule module) {
        super(messages);
        this.module = module;

        playersSpec = parser
                .nonOptions("Players to put into random teams, leave empty for all players online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);

        excludeExtrasSpec = parser
                .acceptsAll(ImmutableList.of("x"), "Players who don't fit into a team will be excluded instead of put into their own team");

        teamCountSpec = parser
                .acceptsAll(ImmutableList.of("c", "count"), "How many teams to create, teams will be as even as possible. Cannot be used with -s")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter().setPredicate(IntegerPredicates.GREATER_THAN_ZERO).setType("Integer > 0"));
        completers.put(teamCountSpec, new FixedValuesTabComplete("4"));

        teamSizeSpec = parser
                .acceptsAll(ImmutableList.of("s", "size"), "How big to attempt make each team. The final team may have less members. Cannot be used with -c")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter().setPredicate(IntegerPredicates.GREATER_THAN_ZERO).setType("Integer > 0"));
        completers.put(teamSizeSpec, new FixedValuesTabComplete("3"));

        excludingSpec = parser
                .acceptsAll(ImmutableList.of("e", "exclude"), "List of players to exclude from selection separated with commas")
                .withRequiredArg()
                .withValuesSeparatedBy(",")
                .withValuesConvertedBy(new OfflinePlayerConverter());
        completers.put(excludingSpec, new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE));
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        int size = -1;
        int count = -1;

        if (options.has(teamCountSpec)) {
            count = teamCountSpec.value(options);
        }

        if (options.has(teamSizeSpec)) {
            size = teamSizeSpec.value(options);
        }

        if ((size == -1 && count == -1) || (size != -1 && count != -1)) {
            sender.sendMessage(messages.getRaw("count or size"));
            return true;
        }

        Set<Player> choice = Sets.newHashSet(playersSpec.values(options));

        // if none are provided then add all online players
        if (choice.size() == 0) {
            choice = Sets.newHashSet(Bukkit.getOnlinePlayers());
        }

        // parse excludes into online players
        Set<Player> excludes = Sets.newHashSet(
                Iterables.filter(
                        Iterables.transform(
                                excludingSpec.values(options),
                                FunctionalUtil.ONLINE_VERSION
                        ),
                        Predicates.notNull()
                )
        );

        // final list with excludes removed and players that already in a team
        List<Player> toAssign = Lists.newArrayList(
                Iterables.filter(
                        Sets.difference(choice, excludes),
                        Predicates.not(PLAYER_HAS_TEAM)
                )
        );

        if (toAssign.size() == 0) {
            sender.sendMessage(messages.getRaw("no players"));
            return true;
        }

        Collections.shuffle(toAssign);

        // calculate team sizes to fit in count teams and assign it to size and carry on as if it was a sized command
        if (count != -1) {
            size = (int) Math.ceil((double) toAssign.size() / (double) count);
        }

        // partition into teams
        List<List<Player>> teams = Lists.newArrayList(Lists.partition(toAssign, size));

        int extras = toAssign.size() % size;

        // if we're excluding leftovers and there were leftovers in a final
        // team, then remove that team from the list. If it's the only team
        // then send a message saying no teams could be created.
        if (options.has(excludeExtrasSpec) && extras > 0) {
            if (teams.size() == 1) {
                sender.sendMessage(messages.evalTemplate("not enough players", ImmutableMap.of("size", size)));
                return true;
            }

            teams.remove(teams.size() - 1);
        }

        // start assigning teams
        for (List<Player> teamPlayers : teams) {
            Optional<Team> optional = module.findFirstEmptyTeam();

            if (!optional.isPresent()) {
                sender.sendMessage(messages.getRaw("not enough teams"));
                return true;
            }

            Team team = optional.get();
            String playerNames = Joiner.on(", ").join(Iterables.transform(teamPlayers, FunctionalUtil.PLAYER_NAME_FETCHER));

            //"teamup notification" : ${colours.command}"You were teamed up into the team {{prefix}}{{name}}"${colours.reset}${colours.command}" with: "${colours.secondary}"{{players}}";

            Map<String, String> context = ImmutableMap.<String, String>builder()
                    .put("prefix", team.getPrefix())
                    .put("name", team.getName())
                    .put("display name", team.getDisplayName())
                    .put("players", playerNames)
                    .build();

            String message = messages.evalTemplate("teamup notification", context);

            // add each player
            for (Player player : teamPlayers) {
                team.addPlayer(player);
                player.sendMessage(message);
            }
        }

        Map<String, Integer> context = ImmutableMap.<String, Integer>builder()
                .put("count", teams.size())
                .put("size", teams.get(0).size())
                .put("players", toAssign.size())
                .build();

        sender.sendMessage(messages.evalTemplate("created", context));

        if (options.has(excludeExtrasSpec) && extras > 0) {
            sender.sendMessage(messages.evalTemplate("extras notice", ImmutableMap.of("extras", extras)));
        }

        return true;
    }

    protected final Predicate<OfflinePlayer> PLAYER_HAS_TEAM = new Predicate<OfflinePlayer>() {
        @Override
        public boolean apply(OfflinePlayer input) {
            return module.getScoreboard().getPlayerTeam(input) != null;
        }
    };
}
