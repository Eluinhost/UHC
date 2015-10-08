package gg.uhc.uhc.modules.team;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;

public class FunctionalUtil {
    public static final Predicate<Team> TEAMS_WITH_PLAYERS = new Predicate<Team>() {
        @Override
        public boolean apply(@Nullable Team input) {
            assert input != null;
            return input.getPlayers().size() > 0;
        }
    };

    public static final Function<OfflinePlayer, String> PLAYER_NAME_FETCHER = new Function<OfflinePlayer, String>() {
        @Nullable
        @Override
        public String apply(OfflinePlayer input) {
            return input.getName();
        }
    };
}
