package gg.uhc.uhc.modules.timer;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static final Map<Character, TimeUnit> UNIT_MAP = ImmutableMap.<Character, TimeUnit>builder()
            .put('d', TimeUnit.DAYS)
            .put('h', TimeUnit.HOURS)
            .put('m', TimeUnit.MINUTES)
            .put('s', TimeUnit.SECONDS)
            .build();

    private static final String UNIT_NAME = "(?<unit>[a-z]+)";
    private static final String UNIT_SIZE = "(?<size>[-\\d]+)";
    private static final String OPTIONAL_WHITESPACE = "\\s*";

    private static final Pattern PART_PATTERN = Pattern.compile("(?:" + UNIT_SIZE + OPTIONAL_WHITESPACE + UNIT_NAME + ")", Pattern.CASE_INSENSITIVE);

    public static Map<TimeUnit, Long> getUnits(String timeString) {
        ImmutableMap.Builder<TimeUnit, Long> map = ImmutableMap.builder();

        Matcher matcher = PART_PATTERN.matcher(timeString);

        while (matcher.find()) {
            String unit = matcher.group("unit").toLowerCase();
            String size = matcher.group("size");

            map.put(UNIT_MAP.get(unit.charAt(0)), Long.parseLong(size));
        }

        return map.build();
    }

    public static long getSeconds(String timeString) {
        Map<TimeUnit, Long> units = getUnits(timeString);

        long seconds = 0;

        for (Map.Entry<TimeUnit, Long> unit : units.entrySet()) {
            seconds += unit.getKey().toSeconds(unit.getValue());
        }

        return seconds;

    }

    public static String secondsToString(long seconds) {
        TimeUnit[] units = TimeUnit.values();

        StringBuilder builder = new StringBuilder();

        boolean negative = false;
        if (seconds < 0) {
            negative = true;
            seconds *= -1;
        }

        for (int i = TimeUnit.DAYS.ordinal(); i >= TimeUnit.SECONDS.ordinal(); i--) {
            TimeUnit unit = units[i];

            long count = unit.convert(seconds, TimeUnit.SECONDS);

            if (count > 0) {
                builder.append(count).append(unit.name().toLowerCase().charAt(0)).append(" ");
                seconds -= unit.toSeconds(count);
            }
        }

        if (builder.length() == 0) {
            return "0s";
        }

        builder.setLength(builder.length() - 1);
        String built = builder.toString();

        if (negative) {
            built = "-" + built;
        }

        return built;
    }
}
