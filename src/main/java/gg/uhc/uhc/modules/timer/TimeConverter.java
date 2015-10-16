package gg.uhc.uhc.modules.timer;


import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.flagcommands.joptsimple.ValueConverter;

public class TimeConverter implements ValueConverter<Long> {

    @Override
    public Long convert(String value) {
        long seconds = TimeUtil.getSeconds(value);

        if (seconds == 0) throw new ValueConversionException("Invalid time supplied, must supply using time format and must be > 0");

        return seconds;
    }

    @Override
    public Class<Long> valueType() {
        return Long.class;
    }

    @Override
    public String valuePattern() {
        return "Time string e.g. 12m30s";
    }
}
