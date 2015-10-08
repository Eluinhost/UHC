package gg.uhc.uhc.modules.health;

import com.google.common.collect.ImmutableList;
import gg.uhc.uhc.command.OptionCommand;
import gg.uhc.uhc.command.converters.EnumConverter;
import gg.uhc.uhc.command.converters.LengthRestricted;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerListHealthCommand extends OptionCommand {

    protected static final String CONFIRMATION = ChatColor.AQUA + "Registered health objective %s (%s) to the slot %s";
    protected static final String UNREGISTERD = ChatColor.AQUA + "Unregistered old objective `%s`";
    protected static final String INCORRECT_TYPE = ChatColor.RED + "Existing objective was of type `%s` when we were looking for `health`. You can use `-f` to register the objective with the correct type";

    protected final Scoreboard scoreboard;

    protected final OptionSpec<Void> forceSpec;
    protected final OptionSpec<String> nameSpec;
    protected final OptionSpec<String> displayNameSpec;
    protected final OptionSpec<DisplaySlot> slotSpec;

    public PlayerListHealthCommand(Scoreboard scoreboard, DisplaySlot defaultSlot, String objectiveName, String displayName) {
        this.scoreboard = scoreboard;

        forceSpec = parser
                .acceptsAll(ImmutableList.of("f", "force"), "Remove any existing objective with the same name.");

        nameSpec = parser
                .acceptsAll(ImmutableList.of("n", "name"), "Name of the objective to create/use.")
                .withRequiredArg()
                .withValuesConvertedBy(new LengthRestricted("objective name", 16))
                .defaultsTo(objectiveName);

        displayNameSpec = parser
                .acceptsAll(ImmutableList.of("d", "displayName"), "Display name of the objective.")
                .withRequiredArg()
                .withValuesConvertedBy(new LengthRestricted("display name", 32))
                .defaultsTo(displayName);

        slotSpec = parser
                .acceptsAll(ImmutableList.of("s", "slot"), "Slot to assign the objective to.")
                .withRequiredArg()
                .withValuesConvertedBy(EnumConverter.forEnum(DisplaySlot.class))
                .defaultsTo(defaultSlot);
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        String objectiveName = nameSpec.value(options);
        String displayName = displayNameSpec.value(options);
        boolean force = options.has(forceSpec);
        DisplaySlot slot = slotSpec.value(options);

        Objective objective = scoreboard.getObjective(objectiveName);

        // unregister the current objective if it exists and we want to force remake it
        if (objective != null && force) {
            sender.sendMessage(String.format(UNREGISTERD, objective.getName()));
            objective.unregister();
            objective = null;
        }

        // register the objective
        if (objective == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "health");

            // add all online player manually
            for (Player player : Bukkit.getOnlinePlayers()) {
                objective.getScore(player.getName()).setScore((int) Math.ceil(player.getHealth()));
            }
        } else {
            // check existing criteria is of correct type
            String existing = objective.getCriteria();

            if (!existing.equals("health")) {
                sender.sendMessage(String.format(INCORRECT_TYPE, existing));
                return true;
            }
        }

        // set display name
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        // set the slot to render in
        objective.setDisplaySlot(slot);

        sender.sendMessage(String.format(CONFIRMATION, objectiveName, displayName, slot.name()));
        return true;
    }
}
