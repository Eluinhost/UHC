package gg.uhc.uhc.modules.teleport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.IntegerConverter;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.converters.WorldConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.uhc.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class TeleportCommand extends OptionCommand {

    protected final OptionSpec<Player> toTeleportSpec;
    protected final OptionSpec<Integer> coordsSpec;
    protected final OptionSpec<World> worldSpec;
    protected final OptionSpec<Player> playerSpec;

    public TeleportCommand() {
        this.toTeleportSpec = parser
                .nonOptions("List of online players to teleport, provide no players to teleport all online")
                .withValuesConvertedBy(new OnlinePlayerConverter());

        this.worldSpec = parser
                .acceptsAll(ImmutableList.of("w", "world"), "The world to go along with the coordinates, defaults to the world the executor is in")
                .withRequiredArg()
                .withValuesConvertedBy(new WorldConverter());

        this.coordsSpec = parser
                .acceptsAll(ImmutableList.of("c", "coords"), "The coords to teleport all specified players to - x,z or x,y,z - does not require -p")
                .withRequiredArg()
                .withValuesConvertedBy(new IntegerConverter().setType("Integer coordinate"))
                .withValuesSeparatedBy(',');

        this.playerSpec = parser
                .acceptsAll(ImmutableList.of("p", "player"), "The name of the player to teleport players to, does not require -c")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerConverter());
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
            sender.sendMessage(ChatColor.RED + "You must provide a player (-p) OR coordinates (-c with optional -w) to teleport to");
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
                    sender.sendMessage(ChatColor.RED + "You must provide a world (-w) as you are not in a world");
                    return true;
                }
            }

            List<Integer> coords = Lists.newArrayList(coordsSpec.values(options));

            if (coords.size() == 2) {
                int y = LocationUtil.findHighestTeleportableY(world, coords.get(0), coords.get(1));

                if (y < 0) {
                    sender.sendMessage(ChatColor.RED + "Couldn't find a suitable Y position for those coordinates, try other coordinates or specify a Y value");
                    return true;
                }

                // set the Y in the coords (+1 as the Y is the block under their feet)
                coords.add(1, y + 1);
            }

            if (coords.size() != 3) {
                sender.sendMessage(ChatColor.RED + "Incorrect coords format use x,z OR x,y,z");
                return true;
            }

            tpLocation = new Location(world, coords.get(0) + .5D, coords.get(1), coords.get(2) + .5D);
        }

        for (Player p : toTeleport) {
            p.teleport(tpLocation);
        }

        sender.sendMessage(ChatColor.AQUA + "Teleported " + toTeleport.size() + " players");
        return true;
    }
}
