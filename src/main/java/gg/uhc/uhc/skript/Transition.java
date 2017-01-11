package gg.uhc.uhc.skript;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

enum Transition {
    ENABLE("enable[d]", 1),
    DISABLE("disable[d]", 2),
    TOGGLE("(change state|toggle[d])", 3);

    static final Map<Integer, Transition> BY_MARK;
    static final String COMBINED_PATTERN;

    static {
        final ImmutableMap.Builder<Integer, Transition> temp = ImmutableMap.builder();

        final StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (final Transition transition : Transition.values()) {
            temp.put(transition.getMark(), transition);
            sb
                .append(transition.getMark())
                .append('¦')
                .append(transition.pattern)
                .append("|");
        }

        sb
            .deleteCharAt(sb.lastIndexOf("|"))
            .append(")");

        BY_MARK = temp.build();
        COMBINED_PATTERN = sb.toString();
    }

    private final String pattern;
    private final int mark;

    Transition(String pattern, int mark) {
        this.pattern = pattern;
        this.mark = mark;
    }

    String getPattern() {
        return pattern;
    }

    int getMark() {
        return mark;
    }
}
