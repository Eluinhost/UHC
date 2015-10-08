package gg.uhc.uhc.modules.team;

import gg.uhc.uhc.command.SubcommandCommand;

public class TeamCommand extends SubcommandCommand {

    public TeamCommand(TeamModule teamModule) {
        registerSubcommand("teamup", new TeamupCommand(teamModule));
    }
}
