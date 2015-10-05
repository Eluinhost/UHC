package gg.uhc.uhc.command.converters;

import gg.uhc.uhc.command.converters.selection.SelectionPredicate;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;


public class IntegerConverter implements ValueConverter<Integer> {

    protected final SelectionPredicate<Integer> predicate;

    public IntegerConverter(SelectionPredicate<Integer> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Integer convert(String value) {
        try {
            int i = Integer.parseInt(value);

            if (!predicate.apply(i)) {
                throw new ValueConversionException("Invalid integer supplied, expected '" + predicate.getTypeString() + "' found: " + value);
            }

            return i;
        } catch (NumberFormatException e) {
            throw new ValueConversionException("Invalid number: " + value, e);
        }
    }

    @Override
    public Class<Integer> valueType() {
        return Integer.class;
    }

    @Override
    public String valuePattern() {
        return predicate.getTypeString();
    }
}
