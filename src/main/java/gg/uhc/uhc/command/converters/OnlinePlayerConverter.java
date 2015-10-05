package gg.uhc.uhc.command.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayerConverter implements ValueConverter<Player> {
    @Override
    public Player convert(String value) {
        Player player = Bukkit.getPlayer(value);

        if (player == null) throw new ValueConversionException("Invalid player: " + value);

        return player;
    }

    @Override
    public Class<Player> valueType() {
        return Player.class;
    }

    @Override
    public String valuePattern() {
        return "player name";
    }
}
