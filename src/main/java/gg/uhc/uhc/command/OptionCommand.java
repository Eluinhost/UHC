package gg.uhc.uhc.command;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import joptsimple.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.List;
import java.util.Set;

public abstract class OptionCommand implements TabExecutor {

    protected Set<String> argumentsNotToTabComplete = Sets.newHashSet("-[arguments]");
    protected static final CommandDequoter dequote = new CommandDequoter();

    protected final OptionParser parser;
    protected final OptionSpec<Void> forHelp;

    public OptionCommand() {
        parser = new OptionParser();

        this.forHelp = parser.acceptsAll(ImmutableList.of("h", "?", "help"), "Displays help messages").forHelp();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            OptionSet options = parser.parse(dequote.dequote(args));

            if (options.has(forHelp)) {
                sendHelp(sender);
                return true;
            }

            return runCommand(sender, options);
        } catch (OptionException ex) {
            String message;

            if (ex.getCause() != null) {
                message = ex.getCause().getMessage();
            } else {
                message = ex.getMessage();
            }

            sender.sendMessage(ChatColor.RED + message + ". Use flag `-?` for help");
            return true;
        }
    }

    protected void sendHelp(CommandSender sender) {
        Set<OptionSpec<?>> specs = Sets.newHashSet(parser.recognizedOptions().values());

        StringBuilder builder = new StringBuilder();
        for (OptionSpec spec : specs) {
            OptionDescriptor desc = (OptionDescriptor) spec;

            builder.setLength(0);
            builder.append(ChatColor.LIGHT_PURPLE);

            if (desc.isRequired()) builder.append(ChatColor.BOLD);

            if (desc.representsNonOptions()) {
                // skip if no description is given for non options
                // because non options always exists
                if (desc.description().equals("")) continue;

                builder.append("Other arguments");
            } else {
                builder.append(desc.options());
            }

            builder.append(ChatColor.RESET);
            builder.append(ChatColor.GRAY);

            builder.append(" - ");

            if (desc.acceptsArguments()) {
                builder.append("Arg: ");
                builder.append(desc.argumentTypeIndicator());
                builder.append(" - ");
            }

            builder.append(desc.description());

            List defaults = desc.defaultValues();

            if (defaults.size() > 0) {
                builder.append(" Defaults: ");
                builder.append(defaults);
            }

            sender.sendMessage(builder.toString());
        }
    }

    protected abstract boolean runCommand(CommandSender sender, OptionSet options);

    protected List<String> runTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String complete = args[args.length - 1];

        if (complete.length() == 0 || complete.charAt(0) != '-') {
            return runTabComplete(sender, args);
        }

        final Set<String> parameters = Sets.newHashSet(Iterables.filter(Lists.newArrayList(args), STARTS_WITH_DASH));

        Set<String> available = Sets.newHashSet();

        for (OptionSpec<?> spec : parser.recognizedOptions().values()) {
            Iterable<String> params = Iterables.transform(spec.options(), PREPEND_DASH);

            boolean add = !Iterables.any(params, new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return argumentsNotToTabComplete.contains(input) || parameters.contains(input);
                }
            });

            if (add) {
                Iterables.addAll(available, params);
            }
        }


        return StringUtil.copyPartialMatches(complete, available, Lists.<String>newArrayList());
    }

    protected static final Predicate<String> STARTS_WITH_DASH = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            return input != null && input.length() > 0 && input.charAt(0) == '-';
        }
    };

    protected static final Function<String, String> PREPEND_DASH = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return input == null ? "-" : '-' + input;
        }
    };
}
