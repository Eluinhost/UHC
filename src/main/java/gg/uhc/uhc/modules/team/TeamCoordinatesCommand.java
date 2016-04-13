/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.TeamCoordinatesCommand
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

import gg.uhc.uhc.messages.MessageTemplates;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Map;

public class TeamCoordinatesCommand implements CommandExecutor {

    protected final MessageTemplates messages;
    protected final TeamModule module;

    public TeamCoordinatesCommand(MessageTemplates messages, TeamModule module) {
        this.messages = messages;
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messages.getRaw("players only"));
            return true;
        }

        final Player player = (Player) sender;

        final Team team = module.getScoreboard().getPlayerTeam(player);

        if (team == null) {
            sender.sendMessage(messages.getRaw("not in team"));
            return true;
        }

        final Location loc = player.getLocation();
        final Map<String, String> context = ImmutableMap.<String, String>builder()
                .put("name", player.getName())
                .put("display name", player.getDisplayName())
                .put("world", loc.getWorld().getName())
                .put("x", String.valueOf(loc.getBlockX()))
                .put("y", String.valueOf(loc.getBlockY()))
                .put("z", String.valueOf(loc.getBlockZ()))
                .build();

        final String message = messages.evalTemplate("format", context);

        for (final OfflinePlayer p : team.getPlayers()) {
            if (p.isOnline()) {
                p.getPlayer().sendMessage(message);
            }
        }

        return true;
    }
}
