/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.FunctionalUtil
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public final class FunctionalUtil {

    public static final Predicate<OfflinePlayer> PLAYERS_ONLINE = new Predicate<OfflinePlayer>() {
        @Override
        public boolean apply(OfflinePlayer input) {
            return input != null && input.isOnline();
        }
    };

    public static final Function<OfflinePlayer, Player> ONLINE_VERSION = new Function<OfflinePlayer, Player>() {
        @Override
        public Player apply(OfflinePlayer input) {
            return input == null ? null : input.getPlayer();
        }
    };

    public static final Predicate<Team> TEAMS_WITH_PLAYERS = new Predicate<Team>() {
        @Override
        public boolean apply(Team input) {
            return input != null && input.getPlayers().size() > 0;
        }
    };

    public static final Function<OfflinePlayer, String> PLAYER_NAME_FETCHER = new Function<OfflinePlayer, String>() {
        @Override
        public String apply(OfflinePlayer input) {
            return input.getName();
        }
    };

    public static final Function<String, OfflinePlayer> OFFLINE_PLAYER_FROM_NAME
            = new Function<String, OfflinePlayer>() {
                @Override
                public OfflinePlayer apply(String input) {
                    return input == null ? null : Bukkit.getOfflinePlayer(input);
                }
            };

    private FunctionalUtil() {}
}
