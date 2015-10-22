/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.prefixes.PrefixColourPredicateConverter
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

import com.google.common.collect.Sets;
import gg.uhc.flagcommands.converters.EnumConverter;
import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.flagcommands.joptsimple.ValueConverter;
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
