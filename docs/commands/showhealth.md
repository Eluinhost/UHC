# `/showhealth`

Shows a health objective in a specific slot. Also add each online player's health to it manually to
fix the Vanilla 0 health bug on non-damaged players.

`-n`, `--name` 

The name of the objective to create/use. If `-f` is also supplied then the objective will
be unregistered and recreated. Defaults to `UHCHealth` if not provided. Check out the percent
health module for the names of the objective for percentage healths.

`-d`, `--displayName`

Change the display name for given objective. Chat colours can be used i.e. `&c`. Using &h will show a heart

`-s`, `--slot`

The slot to put the objective into. Defaults to `PLAYER_LIST` if not provided. Available slots:
`[BELOW_NAME, PLAYER_LIST, SIDEBAR]`

`-f`, `--force`

Force the objective to be remade. This shouldn't be used with percent health objectives as it will break them and the
server will need to be reloaded/restarted for them to work again.


## Examples

`/showhealth`

Shows 0-20 vanilla scoreboard health in the player list

`/showhealth -d "&cPlayer Health" -s SIDEBAR`

Shows 0-20 vanilla scoreboard health in the sidebar with the coloured display name 'Player Health'

`/showhealth -n UHCHealthName -s BELOW_NAME`

Shows 0-100 percent health below the name with the display name 'Health' (using default percent health modules settings
and assuming the percent health module is loaded and enabled)

## Permissions

`uhc.command.showhealth` - allows use of the command, default OP