package gg.uhc.uhc.command.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamConverter implements ValueConverter<Team> {

    protected final Scoreboard scoreboard;

    public TeamConverter(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public Team convert(String value) {
        Team team = scoreboard.getTeam(value);

        if (team == null) throw new ValueConversionException("Invalid team name: " + value);

        return team;
    }

    @Override
    public Class<Team> valueType() {
        return Team.class;
    }

    @Override
    public String valuePattern() {
        return "existing team name";
    }
}
