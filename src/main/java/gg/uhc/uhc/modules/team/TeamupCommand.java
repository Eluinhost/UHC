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
import com.google.common.collect.Iterables;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.OfflinePlayerConverter;
import gg.uhc.flagcommands.converters.StringConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.StringPredicates;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class TeamupCommand extends OptionCommand {

    protected static final String TEAM_DOESNT_EXIST = ChatColor.RED + "The team `%s` does not exist.";
    protected static final String NO_UHC_TEAMS = ChatColor.RED + "No empty UHC teams found, cannot teamup players";
    protected static final String SUPPLY_ONE_PLAYER = ChatColor.RED + "You must supply at least 1 player name to team up";
    protected static final String TEAMUP_NOTIFICATION = ChatColor.AQUA + "You were teamed up into the team %s%s " + ChatColor.RESET + ChatColor.AQUA + "with: " + ChatColor.DARK_PURPLE + "%s";
    protected static final String TEAM_NOT_EMPTY = ChatColor.RED + "That team is not empty, use the add command to add people to existing teams";
    protected static final String TEAMED_UP = ChatColor.AQUA + "Teamed up %d players into team %s%s " + ChatColor.RESET + ChatColor.AQUA + ": %s";

    protected final TeamModule teamModule;

    protected final OptionSpec<String> teamNameSpec;
    protected final OptionSpec<OfflinePlayer> playersSpec;

    public TeamupCommand(TeamModule teamModule) {
        this.teamModule = teamModule;

        teamNameSpec = parser
                .acceptsAll(ImmutableList.of("n", "name", "team"), "Team name of the team to use. If not specified will use the first empty UHC team")
                .withRequiredArg()
                .withValuesConvertedBy(new StringConverter().setPredicate(new StringPredicates.LessThanOrEqualLength(16)).setType("team name (<= 16 chars)"));

        playersSpec = parser
                .nonOptions("List of player names to create a new team from")
                .withValuesConvertedBy(new OfflinePlayerConverter());
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        List<OfflinePlayer> players = playersSpec.values(options);

        if (players.size() == 0) {
            sender.sendMessage(SUPPLY_ONE_PLAYER);
            return true;
        }

        Team team;
        // if they're specifying a team name to use
        if (options.has(teamNameSpec)) {
            String teamName = teamNameSpec.value(options);

            team = teamModule.getScoreboard().getTeam(teamName);

            if (team == null) {
                sender.sendMessage(String.format(TEAM_DOESNT_EXIST, teamName));
                return true;
            }

            if (team.getPlayers().size() > 0) {
                sender.sendMessage(TEAM_NOT_EMPTY);
                return true;
            }
        } else {
            // grab the first UHC team available
            Optional<Team> teamOptional = teamModule.findFirstEmptyTeam();

            if (!teamOptional.isPresent()) {
                sender.sendMessage(NO_UHC_TEAMS);
                return true;
            }

            team = teamOptional.get();
        }

        String members = Joiner.on(", ").join(Iterables.transform(players, FunctionalUtil.PLAYER_NAME_FETCHER));

        String teamNotification = String.format(TEAMUP_NOTIFICATION, team.getPrefix(), team.getDisplayName(), members);

        for (OfflinePlayer player : players) {
            team.addPlayer(player);

            if (player.isOnline()) {
                player.getPlayer().sendMessage(teamNotification);
            }
        }

        sender.sendMessage(String.format(TEAMED_UP, players.size(), team.getPrefix(), team.getDisplayName(), members));
        return true;
    }
}
