package gg.uhc.uhc.command.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class EnumConverter <T extends Enum> implements ValueConverter<T> {

    protected final Class<T> type;

    protected EnumConverter(Class<T> type) {
        this.type = type;
    }

    @Override
    public T convert(String value) {
        try {
            return (T) Enum.valueOf(type, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValueConversionException("Unknown " + type.getSimpleName() + ": " + value, ex);
        }
    }

    @Override
    public Class<T> valueType() {
        return type;
    }

    @Override
    public String valuePattern() {
        return type.getSimpleName();
    }

    public static <B extends Enum> EnumConverter<B> forEnum(Class<B> klass) {
        return new EnumConverter<>(klass);
    }
}
