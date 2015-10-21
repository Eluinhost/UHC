# `/border`

Sets vanilla world borders.

`--reset` 

Resets the border back to default settings

`-s`, `--size`, `-r`, `--radius`

The radius of the border from the centre. Required unless using `--reset`

For shrinking borders use the format `from>to` e.g. `1000>20`. Using a shrinking border requires the use of
the `-t` parameter

`-t`, `--time`

How many seconds to take to move the border to the specified radius when using shrinking border format in `-s`

`-w`, `--world`

The name of the world to set the border in. If not provided defaults to the world you are in when the command is run

`-c`, `--centre`

The centre coordinates of the border in the format `x:z`. Defaults to 0:0 if not provided.

## Examples

`/border -s 1000`

Sets the border to size 1000 in the world you are in

`/border -s 1000>200 -t 600 -w UHC -c 100:-100`

Sets the world border in the world `UHC` to be radius 1000 centred on 100,-100 and shrink to radius 200 over 10 minutes

## Permissions

`uhc.command.border` - allows use of the command, default OP