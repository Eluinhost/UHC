# `/tpp`

Teleports players to another player/location. You must provide `-p` or `-c`

`-w`, `--world`

The name of the world to teleport to when supplying `-c`. If not provided uses the world you are
in when the command is ran

`-c`, `--coords`

The coordinates to teleport to in the format `x,y,z`

`-p`, `--player`

Name of the player to teleport to

`other arguments`

Name of players to teleport, if none provided teleports all online players

## Examples

`/tpp -p Eluinhost`

Teleports all players to Eluinhost

`/tpp -c 0,100,0 ghowden Eluinhost`

Teleports ghowden and Eluinhost to 0,100,0 in the world the command was ran in

`/tpp -c 0,100,0 -w lobby`

Teleports all players to 0,100,0 in the world 'lobby'

## Permissions

`uhc.command.tpp` - allows usage of the command, default OP