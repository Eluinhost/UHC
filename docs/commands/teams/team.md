# `/team`

## `/team teamup`

Takes all of the provided player names and puts them together in a new team.

`-n`, `--name`, `--team`

The name of the team to put the players into. If not specified will use the first
empty UHC team.

`other arguments`

Names of player names to create a team from (do not need to be online)

## `/team add`

Adds all of the player given to the given team

`-t`, `--team`

Name of the team to add all of the player to, required.

`other arguments`

Names of player names to create a team from (do not need to be online)

## `/team remove`

Same as `/team add` but with removing players with one extra:

`-a` - remove all players from the given team

## Permissions

`uhc.command.team` - allows use of the command, default OP