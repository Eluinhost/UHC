/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.ClearTeamsCommand
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

import com.google.common.collect.ImmutableList;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

public class ClearTeamsCommand extends OptionCommand {

    protected static final String COMPLETE = ChatColor.AQUA + "Cleared %d teams of %d players total";

    protected final TeamModule module;

    protected final OptionSpec<Void> allSpec;

    public ClearTeamsCommand(TeamModule module) {
        this.module = module;

        allSpec = parser
                .acceptsAll(ImmutableList.of("a", "all"), "Clears all teams, not just UHC ones");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<Team> teams = options.has(allSpec) ? module.getScoreboard().getTeams() : module.getTeams().values();

        int count = 0;
        for (Team team : teams) {
            for (OfflinePlayer player : team.getPlayers()) {
                team.removePlayer(player);
                count++;
            }
        }

        sender.sendMessage(String.format(COMPLETE, teams.size(), count));
        return true;
    }
}
