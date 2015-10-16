package gg.uhc.uhc.modules.heads;

import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.IntegerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.IntegerPredicates;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GoldenHeadsHealthCommand extends OptionCommand {

    protected final GoldenHeadsModule module;

    protected final OptionSpec<Integer> spec;
    protected final OptionSpec<Void> silentSpec;

    public GoldenHeadsHealthCommand(GoldenHeadsModule module) {
        this.module = module;

        spec = parser.nonOptions("How much HP points to heal total with a golden apple")
                .withValuesConvertedBy(new IntegerConverter().setPredicate(IntegerPredicates.GREATER_THAN_ZERO).setType("Integer > 0"));

        silentSpec = parser.accepts("s", "Sends the response only to you and not the entire server");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        List<Integer> healths = spec.values(options);

        if (healths.size() == 0) {
            sender.sendMessage(ChatColor.RED + "You must provide an Integer to set the amount of health healed to");
            return true;
        }

        module.setHealAmount(healths.get(0));

        String response = ChatColor.AQUA + "Golden heads now heal for " + module.getHealAmount() + " HP";

        if (options.has(silentSpec)) {
            sender.sendMessage(response);
        } else {
            Bukkit.broadcastMessage(response);
        }
        return true;
    }
}
