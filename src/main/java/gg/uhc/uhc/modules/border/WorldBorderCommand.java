package gg.uhc.uhc.modules.border;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.DoubleConverter;
import gg.uhc.flagcommands.converters.LongConverter;
import gg.uhc.flagcommands.converters.WorldConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.DoublePredicates;
import gg.uhc.flagcommands.predicates.LongPredicates;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;

public class WorldBorderCommand extends OptionCommand {

    protected static final String SET_MESSAGE = ChatColor.AQUA + "World border in %s set to radius %.2f centred at %.2f:%.2f";
    protected static final String SHRINKING_MESSAGE = ChatColor.AQUA + "World border will shrink to %.2f in %d seconds";

    protected final OptionSpec<Double> sizeSpec;
    protected final OptionSpec<World> worldSpec;
    protected final OptionSpec<Double> centreSpec;
    protected final OptionSpec<Void> resetSpec;
    protected final OptionSpec<Long> timeSpec;

    public WorldBorderCommand() {
        resetSpec = parser
                .acceptsAll(ImmutableList.of("reset"), "Clears the border back to default settings");

        sizeSpec = parser
                .acceptsAll(ImmutableList.of("s", "size", "r", "radius"), "The radius of the border from the centre. Use 1000>200 format for shrinking borders (requires -t/--time parameter)")
                .requiredUnless(resetSpec)
                .withRequiredArg()
                .withValuesConvertedBy(new DoubleConverter().setPredicate(DoublePredicates.GREATER_THAN_ZERO).setType("Number > 0"))
                .withValuesSeparatedBy('>');

        worldSpec = parser
                .acceptsAll(ImmutableList.of("w", "world"), "The world to create the border in, defaults to the world you are in")
                .withRequiredArg()
                .withValuesConvertedBy(new WorldConverter());

        centreSpec = parser
                .acceptsAll(ImmutableList.of("c", "centre"), "The centre coordinates x:z of the border to create")
                .withRequiredArg()
                .withValuesConvertedBy(new DoubleConverter().setType("coordinate"))
                .withValuesSeparatedBy(':')
                .defaultsTo(new Double[]{0D, 0D});

        timeSpec = parser
                .acceptsAll(ImmutableList.of("t", "time"), "How many seconds to move to the radius given from the previous value")
                .withRequiredArg()
                .withValuesConvertedBy(new LongConverter().setPredicate(LongPredicates.GREATER_THAN_ZERO_INC).setType("Integer > 0"));
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
                sender.sendMessage(ChatColor.RED + "You must provide a world paramter when running from this location");
                return true;
            }
        }

        // check for reset first
        if (options.has(resetSpec)) {
            world.getWorldBorder().reset();
            sender.sendMessage(ChatColor.AQUA + "World border for `" + world.getName() + "` reset.");
            return true;
        }

        List<Double> coords = centreSpec.values(options);
        List<Double> radii = sizeSpec.values(options);

        if (coords.size() != 2) {
            sender.sendMessage(ChatColor.RED + "Invalid coordinates supplied, 2 coordinates must be supplied (x:z)");
            return true;
        }

        if (radii.size() == 0) {
            sender.sendMessage(ChatColor.RED + "Must provide a radius to create the border at");
            return true;
        }

        double radius = radii.get(0);
        Optional<Double> targetRadius;
        long time = 0;

        if (radii.size() == 1) {
            targetRadius = Optional.absent();
        } else {
            if (!options.has(timeSpec)) {
                sender.sendMessage(ChatColor.RED + "You must provide a time parameter when using a shrinking border");
                return true;
            }

            targetRadius = Optional.of(radii.get(1));
            time = timeSpec.value(options);
        }

        WorldBorder border = world.getWorldBorder();

        // set centre
        border.setCenter(coords.get(0), coords.get(1));

        // set initial size
        border.setSize(radius);
        sender.sendMessage(String.format(SET_MESSAGE, world.getName(), radius, coords.get(0), coords.get(1)));

        if (targetRadius.isPresent()) {
            border.setSize(radii.get(1), time);
            sender.sendMessage(String.format(SHRINKING_MESSAGE, radii.get(1), time));
        }

        return true;
    }
}
