# `/randomteams`

Creates random teams from the online players. Either `-c` or `-s` must be used.
Ignores players already in teams.

`-c`, `--count`

How many teams to create. Cannot be used with `-s`

`-s`, `--size`

The size of the teams to create. Cannot be used with `-c`

`-x`

Any leftover players will not be put into a team together and instead will be left out of teams

`-e`, `--exclude`

A comma separated list of players to exclude from putting into teams

## Examples

`/randomteams -c 10`

Creates 10 random teams

`/randomteams -s 10 -x -e Eluinhost,ghowden`

Creates teams of size 10 with extras left out. Ignores Eluinhost and ghowden.

## Permissions

`uhc.command.randomteams` - allows usage of the command, default OP
