package gg.uhc.uhc.modules.commands;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gg.uhc.uhc.command.OptionCommand;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.ModuleRegistry;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ModuleCommand extends OptionCommand {

    public enum Type {
        ENABLE,
        DISABLE,
        TOGGLE
    }

    protected final String PROCESSED = ChatColor.AQUA + "Processed %d/%d modules, new states: %s";
    protected final String PROVIDE_MODULES = ChatColor.AQUA + "You must provide at least 1 module name, available modules: " + ChatColor.DARK_PURPLE + "[%s]";

    protected final ModuleRegistry registry;
    protected final Type type;
    protected final OptionSpec<Map.Entry<String, Module>> moduleSpec;

    public ModuleCommand(ModuleRegistry registry, Type type) {
        this.registry = registry;
        this.type = type;

        moduleSpec = parser.nonOptions("List of module ids to " + type.name().toLowerCase()).withValuesConvertedBy(new ModuleEntryConverter(registry));
    }

    @Override
    protected final boolean runCommand(CommandSender sender, OptionSet options) {
        List<Map.Entry<String, Module>> entries = moduleSpec.values(options);

        if (entries.size() == 0) {
            sender.sendMessage(String.format(PROVIDE_MODULES, Joiner.on(", ").join(Iterables.transform(registry.getModules(), FETCH_KEY_AS_STRING))));
            return true;
        }

        List<String> newStates = Lists.newArrayListWithCapacity(entries.size());
        int count = 0;
        for (Map.Entry<String, Module> entry : entries) {
            Module module = entry.getValue();
            String id = entry.getKey();

            ChatColor stateColour;

            if ((module instanceof DisableableModule)) {
                DisableableModule disableable = (DisableableModule) module;

                switch (type) {
                    case ENABLE:
                        if (!disableable.isEnabled()) {
                            if (disableable.enable()) {
                                disableable.announceState();
                                count++;
                            }
                        }
                        break;
                    case DISABLE:
                        if (disableable.isEnabled()) {
                            if (disableable.disable()) {
                                disableable.announceState();
                                count++;
                            }
                        }
                        break;
                    case TOGGLE:
                        if (disableable.toggle()) {
                            disableable.announceState();
                            count++;
                        }
                        break;
                }

                stateColour = disableable.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
            } else {
                stateColour = ChatColor.GRAY;
            }

            newStates.add(stateColour + id);
        }

        sender.sendMessage(String.format(PROCESSED, count, entries.size(), Joiner.on(", ").join(newStates)));
        return true;
    }

    protected static final Function<Map.Entry, String> FETCH_KEY_AS_STRING = new Function<Map.Entry, String>() {
        @Nullable
        @Override
        public String apply(@Nullable Map.Entry input) {
            return input == null ? "" : input.getKey().toString();
        }
    };
}
