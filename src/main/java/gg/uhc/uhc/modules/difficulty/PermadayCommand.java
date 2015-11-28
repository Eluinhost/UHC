/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.difficulty.PermadayCommand
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

package gg.uhc.uhc.modules.difficulty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.converters.WorldConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.WorldTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PermadayCommand extends TemplatedOptionCommand {

    protected static final String TIME_FREEZE_GAMERULE = "doDaylightCycle";

    protected final OptionSpec<World> worlds;
    protected final OptionSpec<Void> turnOff;

    public PermadayCommand(MessageTemplates messages) {
        super(messages);

        worlds = parser.nonOptions("List of worlds to affect, if none provided runs in the world you are in")
                .withValuesConvertedBy(new WorldConverter());

        nonOptionsTabComplete = new NonDuplicateTabComplete(WorldTabComplete.INSTANCE);

        turnOff = parser.acceptsAll(ImmutableSet.of("u", "o", "off"), "Turns off permaday for the worlds");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        List<World> w = worlds.values(options);

        if (w.size() == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(messages.getRaw("provide world"));
                return true;
            }

            w = Lists.newArrayList(((Player) sender).getWorld());
        }

        boolean on = !options.has(turnOff);

        for (World world : w) {
            if (on) {
                world.setGameRuleValue(TIME_FREEZE_GAMERULE, "false");
                world.setTime(6000);
            } else {
                world.setGameRuleValue(TIME_FREEZE_GAMERULE, "true");
            }

            String message = messages.evalTemplate(on ? "on notification" : "off notification", ImmutableMap.of("world", world.getName()));
            for (Player player : world.getPlayers()) {
                player.sendMessage(message);
            }
        }

        sender.sendMessage(messages.evalTemplate(on ? "on completed" : "off completed", ImmutableMap.of("count", w.size())));
        return true;
    }
}
