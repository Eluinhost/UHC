package gg.uhc.uhc.modules.team;


import gg.uhc.flagcommands.commands.SubcommandCommand;

public class TeamCommands extends SubcommandCommand {

    public TeamCommands(TeamModule teamModule) {
        registerSubcommand("teamup", new TeamupCommand(teamModule));
        registerSubcommand("add", new TeamAddCommand(teamModule));
        registerSubcommand("remove", new TeamRemoveCommand(teamModule));
    }
}
