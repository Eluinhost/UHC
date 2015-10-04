package gg.uhc.uhc.command.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class LengthRestricted implements ValueConverter<String> {

    protected final String identifier;
    protected final int length;

    public LengthRestricted(String identifier, int length) {
        this.identifier = identifier;
        this.length = length;
    }

    @Override
    public String convert(String s) {
        if (s.length() > length) throw new ValueConversionException(identifier + " too long (" + length + " max): " + s);

        return s;
    }

    @Override
    public Class<? extends String> valueType() {
        return String.class;
    }

    @Override
    public String valuePattern() {
        return "String <= " + length + " chars";
    }
}
