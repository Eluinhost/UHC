/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.WorldMatcher
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

package gg.uhc.uhc.modules;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

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
        @Override
        public String apply(String input) {
            return input == null ? null : input.toLowerCase();
        }
    };
}
