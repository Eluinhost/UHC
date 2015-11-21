/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.TeamupCommand
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import gg.uhc.flagcommands.converters.OfflinePlayerConverter;
import gg.uhc.flagcommands.converters.StringConverter;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.StringPredicates;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import gg.uhc.flagcommands.tab.TeamNameTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;

public class TeamupCommand extends TemplatedOptionCommand {

    protected final TeamModule teamModule;

    protected final ArgumentAcceptingOptionSpec<String> teamNameSpec;
    protected final OptionSpec<OfflinePlayer> playersSpec;

    public TeamupCommand(MessageTemplates messages, TeamModule teamModule) {
        super(messages);
        this.teamModule = teamModule;

        teamNameSpec = parser
                .acceptsAll(ImmutableList.of("n", "name", "team"), "Team name of the team to use. If not specified will use the first empty UHC team")
                .withRequiredArg()
                .withValuesConvertedBy(new StringConverter().setPredicate(new StringPredicates.LessThanOrEqualLength(16)).setType("team name (<= 16 chars)"));
        completers.put(teamNameSpec, new TeamNameTabComplete(teamModule.getScoreboard()));

        playersSpec = parser
                .nonOptions("List of player names to create a new team from")
                .withValuesConvertedBy(new OfflinePlayerConverter());
        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        List<OfflinePlayer> players = playersSpec.values(options);

        if (players.size() == 0) {
            sender.sendMessage(messages.getRaw("supply one player"));
            return true;
        }

        Team team;
        // if they're specifying a team name to use
        if (options.has(teamNameSpec)) {
            String teamName = teamNameSpec.value(options);

            team = teamModule.getScoreboard().getTeam(teamName);

            if (team == null) {
                sender.sendMessage(messages.evalTemplate("doesnt exist", ImmutableMap.of("name", teamName)));
                return true;
            }

            if (team.getPlayers().size() > 0) {
                sender.sendMessage(messages.getRaw("not empty"));
                return true;
            }
        } else {
            // grab the first UHC team available
            Optional<Team> teamOptional = teamModule.findFirstEmptyTeam();

            if (!teamOptional.isPresent()) {
                sender.sendMessage(messages.getRaw("no uhc teams"));
                return true;
            }

            team = teamOptional.get();
        }

        String members = Joiner.on(", ").join(Iterables.transform(players, FunctionalUtil.PLAYER_NAME_FETCHER));

        Map<String, String> context = ImmutableMap.<String, String>builder()
                .put("prefix", team.getPrefix())
                .put("name", team.getName())
                .put("display name", team.getDisplayName())
                .put("players", members)
                .put("count", String.valueOf(players.size()))
                .build();

        String teamNotification = messages.evalTemplate("teamup notification", context);

        for (OfflinePlayer player : players) {
            team.addPlayer(player);

            if (player.isOnline()) {
                player.getPlayer().sendMessage(teamNotification);
            }
        }

        sender.sendMessage(messages.evalTemplate("completed", context));
        return true;
    }
}
