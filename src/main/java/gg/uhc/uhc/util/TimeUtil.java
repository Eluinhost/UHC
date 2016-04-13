/*
 * Project: UHC
 * Class: gg.uhc.uhc.util.TimeUtil
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

package gg.uhc.uhc.util;

import com.google.common.collect.ImmutableMap;

import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    private static final Map<Character, TimeUnit> UNIT_MAP = ImmutableMap.<Character, TimeUnit>builder()
            .put('d', TimeUnit.DAYS)
            .put('h', TimeUnit.HOURS)
            .put('m', TimeUnit.MINUTES)
            .put('s', TimeUnit.SECONDS)
            .build();

    private static final String UNIT_NAME = "(?<unit>[a-z]+)";
    private static final String UNIT_SIZE = "(?<size>[-\\d]+)";
    private static final String OPTIONAL_WHITESPACE = "\\s*";

    private static final NumberFormat FORMATTER = NumberFormat.getInstance();

    static {
        FORMATTER.setParseIntegerOnly(true);
        FORMATTER.setMinimumIntegerDigits(2);
    }

    private static final Pattern PART_PATTERN = Pattern.compile(
            "(?:" + UNIT_SIZE + OPTIONAL_WHITESPACE + UNIT_NAME + ")",
            Pattern.CASE_INSENSITIVE
    );

    private TimeUtil() {}

    public static Map<TimeUnit, Long> getUnits(String timeString) {
        final ImmutableMap.Builder<TimeUnit, Long> map = ImmutableMap.builder();

        final Matcher matcher = PART_PATTERN.matcher(timeString);

        while (matcher.find()) {
            final String unit = matcher.group("unit").toLowerCase();
            final String size = matcher.group("size");

            final TimeUnit type = UNIT_MAP.get(unit.charAt(0));

            if (type == null) continue;

            map.put(type, Long.parseLong(size));
        }

        return map.build();
    }

    public static long getSeconds(String timeString) {
        final Map<TimeUnit, Long> units = getUnits(timeString);

        long seconds = 0;

        for (final Map.Entry<TimeUnit, Long> unit : units.entrySet()) {
            seconds += unit.getKey().toSeconds(unit.getValue());
        }

        return seconds;

    }

    public static String secondsToString(long toConvert) {
        long seconds = toConvert;
        final TimeUnit[] units = TimeUnit.values();

        final StringBuilder builder = new StringBuilder();

        boolean negative = false;
        if (seconds < 0) {
            negative = true;
            seconds *= -1;
        }

        for (int i = TimeUnit.DAYS.ordinal(); i >= TimeUnit.SECONDS.ordinal(); i--) {
            final TimeUnit unit = units[i];

            final long count = unit.convert(seconds, TimeUnit.SECONDS);

            if (count > 0) {
                builder.append(FORMATTER.format(count))
                        .append(unit.name().toLowerCase().charAt(0))
                        .append(" ");
                seconds -= unit.toSeconds(count);
            }
        }

        if (builder.length() == 0) {
            return "00s";
        }

        builder.setLength(builder.length() - 1);
        String built = builder.toString();

        if (negative) {
            built = "-" + built;
        }

        return built;
    }
}
