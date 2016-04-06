/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.border.WorldBorderCommand
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

package gg.uhc.uhc.modules.border;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gg.uhc.flagcommands.converters.DoubleConverter;
import gg.uhc.flagcommands.converters.LongConverter;
import gg.uhc.flagcommands.converters.WorldConverter;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.DoublePredicates;
import gg.uhc.flagcommands.predicates.LongPredicates;
import gg.uhc.flagcommands.tab.FixedValuesTabComplete;
import gg.uhc.flagcommands.tab.WorldTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;

public class WorldBorderCommand extends TemplatedOptionCommand {

    protected final ArgumentAcceptingOptionSpec<Double> sizeSpec;
    protected final ArgumentAcceptingOptionSpec<World> worldSpec;
    protected final ArgumentAcceptingOptionSpec<Double> centreSpec;
    protected final OptionSpec<Void> resetSpec;
    protected final ArgumentAcceptingOptionSpec<Long> timeSpec;

    public WorldBorderCommand(MessageTemplates messages) {
        super(messages);

        resetSpec = parser
                .acceptsAll(ImmutableList.of("reset"), "Clears the border back to default settings");

        sizeSpec = parser
                .acceptsAll(ImmutableList.of("s", "size", "r", "radius"), "The radius of the border from the centre. Use 1000>200 format for shrinking borders (requires -t/--time parameter)")
                .requiredUnless(resetSpec)
                .withRequiredArg()
                .withValuesConvertedBy(new DoubleConverter().setPredicate(DoublePredicates.GREATER_THAN_ZERO).setType("Number > 0"))
                .withValuesSeparatedBy('>');
        completers.put(sizeSpec, new FixedValuesTabComplete("250", "500", "1000", "1000>200"));

        worldSpec = parser
                .acceptsAll(ImmutableList.of("w", "world"), "The world to create the border in, defaults to the world you are in")
                .withRequiredArg()
                .withValuesConvertedBy(new WorldConverter());
        completers.put(worldSpec, WorldTabComplete.INSTANCE);

        centreSpec = parser
                .acceptsAll(ImmutableList.of("c", "centre"), "The centre coordinates x:z of the border to create")
                .withRequiredArg()
                .withValuesConvertedBy(new DoubleConverter().setType("coordinate"))
                .withValuesSeparatedBy(':')
                .defaultsTo(new Double[]{0D, 0D});
        completers.put(centreSpec, new FixedValuesTabComplete("0:0"));

        timeSpec = parser
                .acceptsAll(ImmutableList.of("t", "time"), "How many seconds to move to the radius given from the previous value")
                .withRequiredArg()
                .withValuesConvertedBy(new LongConverter().setPredicate(LongPredicates.GREATER_THAN_ZERO_INC).setType("Integer > 0"));
        completers.put(timeSpec, new FixedValuesTabComplete("300", "600", "900", "1200"));
    }

    protected Optional<World> getWorld(CommandSender sender) {
        if (sender instanceof Entity) {
            return Optional.of(((Entity) sender).getWorld());
        }

        if (sender instanceof BlockCommandSender) {
            return Optional.of(((BlockCommandSender) sender).getBlock().getWorld());
        }

        return Optional.absent();
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        World world;
        // grab the world
        if (options.has(worldSpec)) {
            world = worldSpec.value(options);
        } else {
            Optional<World> w = getWorld(sender);

            if (w.isPresent()) {
                world = w.get();
            } else {
                sender.sendMessage(messages.getRaw("provide world"));
                return true;
            }
        }

        // check for reset first
        if (options.has(resetSpec)) {
            world.getWorldBorder().reset();
            sender.sendMessage(messages.evalTemplate("reset", ImmutableMap.of("world", world.getName())));
            return true;
        }

        List<Double> coords = centreSpec.values(options);
        List<Double> radii = sizeSpec.values(options);

        if (coords.size() != 2) {
            sender.sendMessage(messages.getRaw("invalid coords"));
            return true;
        }

        if (radii.size() == 0) {
            sender.sendMessage(messages.getRaw("provide radius"));
            return true;
        }

        double radius = radii.get(0);
        Optional<Double> targetRadius;
        long time = 0;

        if (radii.size() == 1) {
            targetRadius = Optional.absent();
        } else {
            if (!options.has(timeSpec)) {
                sender.sendMessage(messages.getRaw("provide time"));
                return true;
            }

            targetRadius = Optional.of(radii.get(1));
            time = timeSpec.value(options);
        }

        WorldBorder border = world.getWorldBorder();

        // set centre
        border.setCenter(coords.get(0), coords.get(1));

        // set initial size
        border.setSize(radius * 2.0D);
        sender.sendMessage(messages.evalTemplate("set regular", ImmutableMap.of("world", world.getName(), "radius", radius, "x", coords.get(0), "z", coords.get(1))));

        if (targetRadius.isPresent()) {
            border.setSize(radii.get(1) * 2.0D, time);
            sender.sendMessage(messages.evalTemplate("set shrinking", ImmutableMap.of("radius", radii.get(1), "seconds", time)));
        }

        return true;
    }
}
