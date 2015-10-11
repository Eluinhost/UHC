package gg.uhc.uhc.modules;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class WorldMatcher {

    protected static final String WORLDS_KEY = "worlds";
    protected static final String IS_WHITELIST_KEY = "worlds are whitelist";


    protected Set<String> worlds;
    protected boolean isWhitelist;

    public WorldMatcher(ConfigurationSection section, List<String> worldsDefault, boolean isWhitelistDefault) {
        if (!section.contains(WORLDS_KEY)) {
            section.set(WORLDS_KEY, worldsDefault);
        }

        if (!section.contains(IS_WHITELIST_KEY)) {
            section.set(IS_WHITELIST_KEY, isWhitelistDefault);
        }

        worlds = ImmutableSet.copyOf(Iterables.filter(Iterables.transform(section.getStringList(WORLDS_KEY), TO_LOWER_CASE), Predicates.notNull()));
        isWhitelist = section.getBoolean(IS_WHITELIST_KEY);
    }

    public boolean worldMatches(World world) {
        return this.worldMatches(world.getName());
    }

    public boolean worldMatches(String world) {
        boolean contained = worlds.contains(world.toLowerCase());

        return isWhitelist ? contained : !contained;
    }

    protected static final Function<String, String> TO_LOWER_CASE = new Function<String, String>() {
        @Nullable
        @Override
        public String apply(String input) {
            return input == null ? null : input.toLowerCase();
        }
    };
}
