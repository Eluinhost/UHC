package gg.uhc.uhc.modules.team;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class FunctionalUtil {
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

    public static final Function<String, OfflinePlayer> OFFLINE_PLAYER_FROM_NAME = new Function<String, OfflinePlayer>() {
        @Override
        public OfflinePlayer apply(String input) {
            return input == null ? null : Bukkit.getOfflinePlayer(input);
        }
    };
}
