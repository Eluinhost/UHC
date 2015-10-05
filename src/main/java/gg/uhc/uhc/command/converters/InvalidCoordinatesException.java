package gg.uhc.uhc.command.converters;

import joptsimple.OptionException;

import java.util.Collection;

public class InvalidCoordinatesException extends OptionException {
    public InvalidCoordinatesException(Collection<String> options) {
        super(options);
    }

    @Override
    public String getMessage() {
        return "Invalid coordinates (require 2 numbers x:z) " + multipleOptionMessage();
    }
}
