/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.NoTeamCommand
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoTeamCommand implements CommandExecutor {

    protected final MessageTemplates messages;
    protected final TeamModule module;

    public NoTeamCommand(MessageTemplates messages, TeamModule module) {
        this.messages = messages;
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Iterable<? extends Player> noTeam = Iterables.filter(Bukkit.getOnlinePlayers(), Predicates.not(HAS_TEAM));

        String noTeamNames = Joiner.on(", ").join(Iterables.transform(noTeam, FunctionalUtil.PLAYER_NAME_FETCHER));

        if (noTeamNames.length() == 0) noTeamNames = messages.getRaw("none");

        sender.sendMessage(messages.evalTemplate("list", ImmutableMap.of("players", noTeamNames)));
        return true;
    }

    protected final Predicate<Player> HAS_TEAM = new Predicate<Player>() {
        @Override
        public boolean apply(Player input) {
            return module.getScoreboard().getPlayerTeam(input) != null;
        }
    };
}
