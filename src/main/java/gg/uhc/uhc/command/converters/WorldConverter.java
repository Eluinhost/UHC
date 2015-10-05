package gg.uhc.uhc.command.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldConverter implements ValueConverter<World> {
    @Override
    public World convert(String value) {
        World world = Bukkit.getWorld(value);

        if (world == null) throw new ValueConversionException("World does not exist: " + value);

        return world;
    }

    @Override
    public Class<World> valueType() {
        return World.class;
    }

    @Override
    public String valuePattern() {
        return "world name";
    }
}
