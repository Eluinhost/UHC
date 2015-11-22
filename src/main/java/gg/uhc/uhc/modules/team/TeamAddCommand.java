/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.TeamAddCommand
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import gg.uhc.flagcommands.converters.OfflinePlayerConverter;
import gg.uhc.flagcommands.converters.TeamConverter;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import gg.uhc.flagcommands.tab.TeamNameTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class TeamAddCommand extends TemplatedOptionCommand {

    protected final ArgumentAcceptingOptionSpec<Team> teamSpec;
    protected final OptionSpec<OfflinePlayer> playersSpec;

    public TeamAddCommand(MessageTemplates messages, TeamModule module) {
        super(messages);
        teamSpec = parser
                .acceptsAll(ImmutableList.of("t", "team"), "Name of the team to add the players to")
                .withRequiredArg()
                .required()
                .withValuesConvertedBy(new TeamConverter(module.getScoreboard()));

        playersSpec = parser
                .nonOptions("List of player names to add to the specified team")
                .withValuesConvertedBy(new OfflinePlayerConverter());

        completers.put(teamSpec, new TeamNameTabComplete(module.getScoreboard()));
        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Team team = teamSpec.value(options);
        Set<OfflinePlayer> players = Sets.newHashSet(playersSpec.values(options));
        players.removeAll(team.getPlayers());

        for (OfflinePlayer player : players) {
            team.addPlayer(player);
        }

        Set<OfflinePlayer> finalTeam = team.getPlayers();

        String members = finalTeam.size() == 0 ? ChatColor.DARK_GRAY + "No members" : Joiner.on(", ").join(Iterables.transform(team.getPlayers(), FunctionalUtil.PLAYER_NAME_FETCHER));

        sender.sendMessage(messages.evalTemplate("added", ImmutableMap.of("count", players.size(), "players", members)));
        return false;
    }
}
