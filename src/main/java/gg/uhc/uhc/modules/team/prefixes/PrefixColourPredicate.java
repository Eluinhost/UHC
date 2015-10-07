package gg.uhc.uhc.modules.team.prefixes;


import com.google.common.base.Predicate;
import org.bukkit.ChatColor;

import java.util.Set;

public class PrefixColourPredicate implements Predicate<Prefix> {

    protected final boolean exactMatch;
    protected final Set<ChatColor> toMatch;

    public PrefixColourPredicate(boolean exactMatchOnly, Set<ChatColor> matching) {
        this.exactMatch = exactMatchOnly;
        this.toMatch = matching;
    }

    @Override
    public boolean apply(Prefix input) {
        return input.containsColours(exactMatch, toMatch);
    }
}
