package gg.uhc.uhc.command;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

public class CommandDequoter {

    protected static final char ESCAPE = '\\';
    protected static final char QUOTE = '"';
    protected static final char SPACE = ' ';

    protected boolean inQuote;
    protected boolean escaped;
    protected StringBuilder builder;
    protected List<String> dequoted;

    protected void processChar(char character) {
        switch (character) {
            case QUOTE:
                if (escaped) {
                    stopEscape(QUOTE);
                } else if (inQuote) {
                    inQuote = false;
                    addNewDequoted();
                } else {
                    inQuote = true;
                }
                break;
            case SPACE:
                if (escaped) {
                    stopEscape(SPACE);
                } else if (inQuote) {
                    builder.append(SPACE);
                } else {
                    addNewDequoted();
                }
                break;
            case ESCAPE:
                if (escaped) {
                    stopEscape(ESCAPE);
                } else {
                    escaped = true;
                }
                break;
            default:
                if (escaped) {
                    stopEscape(ESCAPE);
                }
                builder.append(character);
        }
    }

    protected void stopEscape(char character) {
        builder.append(character);
        escaped = false;
    }

    protected void addNewDequoted() {
        if (builder.length() != 0) {
            dequoted.add(builder.toString());
            builder.setLength(0);
        }
    }

    public String[] dequote(String[] args) {
        char[] process = Joiner.on(" ").join(args).toCharArray();

        // reset vars
        inQuote = false;
        escaped = false;
        builder = new StringBuilder();
        dequoted = Lists.newArrayListWithCapacity(args.length);

        for (char proces : process) {
            processChar(proces);
        }

        // handle final token
        addNewDequoted();

        return dequoted.toArray(new String[dequoted.size()]);
    }
}
