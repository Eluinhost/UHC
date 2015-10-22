/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.prefixes.Prefix
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

package gg.uhc.uhc.modules.team.prefixes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;

import java.util.Set;

public class Prefix {

    protected final ChatColor colour;
    protected final Set<ChatColor> formatting;

    public Prefix(ChatColor colour, Set<ChatColor> formatting) {
        this.colour = colour;
        this.formatting = formatting;
    }

    public Prefix(ChatColor colour, ChatColor... formatting) {
        this(colour, ImmutableSet.copyOf(formatting));
    }

    public int count() {
        return 1 + formatting.size();
    }

    public boolean containsColours(boolean exact, ChatColor... colors) {
        return this.containsColours(exact, Sets.newHashSet(colors));
    }

    public boolean containsColours(boolean exact, Set<ChatColor> colours) {
        if (exact && colours.size() != count()) return false;

        Set<ChatColor> toMatch = Sets.newHashSet(colours);

        boolean hadColour = toMatch.remove(colour);

        // if it didn't have the colour it wasn't an exact match
        if (exact && !hadColour) return false;

        // check that all the stuff left is in formatting
        return formatting.containsAll(toMatch);
    }
}
