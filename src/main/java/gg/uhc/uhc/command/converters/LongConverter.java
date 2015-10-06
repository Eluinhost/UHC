package gg.uhc.uhc.command.converters;

import gg.uhc.uhc.command.converters.selection.SelectionPredicate;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class LongConverter implements ValueConverter<Long> {

    protected final SelectionPredicate<Long> predicate;

    public LongConverter(SelectionPredicate<Long> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Long convert(String value) {
        try {
            long i = Long.parseLong(value);

            if (!predicate.apply(i)) {
                throw new ValueConversionException("Invalid number supplied, expected '" + predicate.getTypeString() + "' found: " + value);
            }

            return i;
        } catch (NumberFormatException e) {
            throw new ValueConversionException("Invalid number: " + value, e);
        }
    }

    @Override
    public Class<Long> valueType() {
        return Long.class;
    }

    @Override
    public String valuePattern() {
        return predicate.getTypeString();
    }
}
