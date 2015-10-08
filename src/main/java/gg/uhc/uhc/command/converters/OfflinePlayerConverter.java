package gg.uhc.uhc.command.converters;

import joptsimple.ValueConverter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerConverter implements ValueConverter<OfflinePlayer> {
    @Override
    public OfflinePlayer convert(String value) {
        return Bukkit.getOfflinePlayer(value);
    }

    @Override
    public Class<OfflinePlayer> valueType() {
        return OfflinePlayer.class;
    }

    @Override
    public String valuePattern() {
        return "player name";
    }
}
