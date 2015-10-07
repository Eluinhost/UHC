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
