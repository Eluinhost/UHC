package gg.uhc.uhc.command.converters;

import gg.uhc.uhc.command.converters.selection.SelectionPredicate;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class DoubleConverter implements ValueConverter<Double> {

    protected final SelectionPredicate<Double> predicate;

    public DoubleConverter(SelectionPredicate<Double> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Double convert(String value) {
        try {
            double i = Double.parseDouble(value);

            if (!predicate.apply(i)) {
                throw new ValueConversionException("Invalid number supplied, expected '" + predicate.getTypeString() + "' found: " + value);
            }

            return i;
        } catch (NumberFormatException e) {
            throw new ValueConversionException("Invalid number: " + value, e);
        }
    }

    @Override
    public Class<Double> valueType() {
        return Double.class;
    }

    @Override
    public String valuePattern() {
        return predicate.getTypeString();
    }
}
