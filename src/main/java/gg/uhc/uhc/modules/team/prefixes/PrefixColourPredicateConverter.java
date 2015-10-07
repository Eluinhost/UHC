package gg.uhc.uhc.modules.team.prefixes;

import com.google.common.collect.Sets;
import gg.uhc.uhc.command.converters.EnumConverter;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.ChatColor;

import java.util.Set;

public class PrefixColourPredicateConverter implements ValueConverter<PrefixColourPredicate> {

    protected final EnumConverter<ChatColor> colourConverter = EnumConverter.forEnum(ChatColor.class);

    @Override
    public PrefixColourPredicate convert(String value) {
        boolean exact = false;

        if (value.length() > 0 && value.charAt(0) == '=') {
            exact = true;
            value = value.substring(1);
        }

        String[] parts = value.split("\\+");

        if (parts.length == 0) throw new ValueConversionException("Must supply at least 1 formatting code to filter out");

        Set<ChatColor> colours = Sets.newHashSetWithExpectedSize(parts.length);

        for (String part : parts) {
            colours.add(colourConverter.convert(part));
        }

        return new PrefixColourPredicate(exact, colours);
    }

    @Override
    public Class<PrefixColourPredicate> valueType() {
        return PrefixColourPredicate.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}
