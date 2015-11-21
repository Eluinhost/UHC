/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.teleport.TeleportCommand
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

package gg.uhc.uhc.modules.teleport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.converters.IntegerConverter;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.converters.WorldConverter;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.FixedValuesTabComplete;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import gg.uhc.flagcommands.tab.WorldTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import gg.uhc.uhc.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class TeleportCommand extends TemplatedOptionCommand {

    protected final OptionSpec<Player> toTeleportSpec;
    protected final ArgumentAcceptingOptionSpec<Integer> coordsSpec;
    protected final ArgumentAcceptingOptionSpec<World> worldSpec;
    protected final ArgumentAcceptingOptionSpec<Player> playerSpec;

    public TeleportCommand(MessageTemplates messages) {
        super(messages);

        this.toTeleportSpec = parser
                .nonOptions("List of online players to teleport, provide no players to teleport all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());
        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);

        this.worldSpec = parser
                .acceptsAll(ImmutableList.of("w", "world"), "The world to go along with the coordinates, defaults to the world the executor is in")
                .withRequiredArg()
                .withValuesConvertedBy(new WorldConverter());
        completers.put(worldSpec, new NonDuplicateTabComplete(WorldTabComplete.INSTANCE));

        this.coordsSpec = parser
                .acceptsAll(ImmutableList.of("c", "coords"), "The coords to teleport all specified players to - x,z or x,y,z - does not require -p")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter().setType("Integer coordinate"))
                .withValuesSeparatedBy(',');
        completers.put(coordsSpec, new FixedValuesTabComplete("0,100,0"));

        this.playerSpec = parser
                .acceptsAll(ImmutableList.of("p", "player"), "The name of the player to teleport players to, does not require -c")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerConverter());
        completers.put(playerSpec, OnlinePlayerTabComplete.INSTANCE);
    }


    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> toTeleport = toTeleportSpec.values(options);

        if (toTeleport.size() == 0) {
            toTeleport = Bukkit.getOnlinePlayers();
        }

        boolean isPlayer = options.has(playerSpec);
        boolean isCoords = options.has(coordsSpec);

        if ((isPlayer && isCoords) || (!isPlayer && !isCoords)) {
            sender.sendMessage(messages.getRaw("invalid flag"));
            return true;
        }

        Location tpLocation;

        if (isPlayer) {
            tpLocation = playerSpec.value(options).getLocation();
        } else {
            World world;

            if (options.has(worldSpec)) {
                world = worldSpec.value(options);
            } else {
                if (sender instanceof Entity) {
                    world = ((Entity) sender).getWorld();
                } else {
                    sender.sendMessage(messages.getRaw("provide world"));
                    return true;
                }
            }

            List<Integer> coords = Lists.newArrayList(coordsSpec.values(options));

            if (coords.size() == 2) {
                int y = LocationUtil.findHighestTeleportableY(world, coords.get(0), coords.get(1));

                if (y < 0) {
                    sender.sendMessage(messages.getRaw("no suitable Y"));
                    return true;
                }

                // set the Y in the coords (+1 as the Y is the block under their feet)
                coords.add(1, y + 1);
            }

            if (coords.size() != 3) {
                sender.sendMessage(messages.getRaw("invalid coordinates"));
                return true;
            }

            tpLocation = new Location(world, coords.get(0) + .5D, coords.get(1), coords.get(2) + .5D);
        }

        for (Player p : toTeleport) {
            p.teleport(tpLocation);
        }

        sender.sendMessage(messages.evalTemplate("teleported", ImmutableMap.of("count", toTeleport.size())));
        return true;
    }
}
