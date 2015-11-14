/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.health.PlayerListHealthCommand
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

package gg.uhc.uhc.modules.health;

import com.google.common.collect.ImmutableList;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.EnumConverter;
import gg.uhc.flagcommands.converters.StringConverter;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.StringPredicates;
import gg.uhc.flagcommands.tab.EnumTabComplete;
import gg.uhc.flagcommands.tab.FixedValuesTabComplete;
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
    protected final PercentHealthObjectiveModule percentHealth;

    protected final OptionSpec<Void> forceSpec;
    protected final OptionSpec<Void> percentSpec;
    protected final ArgumentAcceptingOptionSpec<String> nameSpec;
    protected final ArgumentAcceptingOptionSpec<String> displayNameSpec;
    protected final ArgumentAcceptingOptionSpec<DisplaySlot> slotSpec;

    public PlayerListHealthCommand(PercentHealthObjectiveModule percentHealth, Scoreboard scoreboard, DisplaySlot defaultSlot, String objectiveName, String displayName) {
        this.percentHealth = percentHealth;
        this.scoreboard = scoreboard;

        forceSpec = parser
                .acceptsAll(ImmutableList.of("f", "force"), "Remove any existing objective with the same name. Does not work with percent health flag");

        nameSpec = parser
                .acceptsAll(ImmutableList.of("n", "name"), "Name of the objective to create/use. Use -p/percent to use the objective from the percent health module")
                .withRequiredArg()
                .withValuesConvertedBy(new StringConverter().setPredicate(new StringPredicates.LessThanOrEqualLength(16)).setType("objective name (<= 16 chars)"))
                .defaultsTo(objectiveName);
        completers.put(nameSpec, new FixedValuesTabComplete(objectiveName));

        percentSpec = parser
                .acceptsAll(ImmutableList.of("p", "percent"), "Use the objective from the percent health module");

        displayNameSpec = parser
                .acceptsAll(ImmutableList.of("d", "displayName"), "Change the display name of the objective.")
                .withRequiredArg()
                .withValuesConvertedBy(new StringConverter().setPredicate(new StringPredicates.LessThanOrEqualLength(32)).setType("display name (<= 32 chars)"));
        completers.put(displayNameSpec, new FixedValuesTabComplete(displayName));

        slotSpec = parser
                .acceptsAll(ImmutableList.of("s", "slot"), "Slot to assign the objective to.")
                .withRequiredArg()
                .withValuesConvertedBy(EnumConverter.forEnum(DisplaySlot.class))
                .defaultsTo(defaultSlot);
        completers.put(slotSpec, new EnumTabComplete(DisplaySlot.class));
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        String objectiveName = nameSpec.value(options);
        boolean force = options.has(forceSpec);
        DisplaySlot slot = slotSpec.value(options);

        Objective objective;

        if (options.has(percentSpec)) {
            if (percentHealth == null) {
                sender.sendMessage(ChatColor.RED + "Cannot use percent health as the module was not loaded");
                return true;
            }

            objective = percentHealth.getObjective();
        } else {
            objective = scoreboard.getObjective(objectiveName);

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
        }

        // set display name if needed
        if (options.has(displayNameSpec)) {
            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayNameSpec.value(options)));
        }

        // set the slot to render in
        objective.setDisplaySlot(slot);

        sender.sendMessage(String.format(CONFIRMATION, objective.getName(), objective.getDisplayName(), slot.name()));
        return true;
    }
}
