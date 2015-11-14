# `/showhealth`

Shows a health objective in a specific slot. Also add each online player's health to it manually to
fix the Vanilla 0 health bug on non-damaged players.

`-n`, `--name` 

The name of the objective to create/use. If `-f` is also supplied then the objective will
be unregistered and recreated. Defaults to `UHCHealth` if not provided.

`-d`, `--displayName`

Change the display name for given objective. Chat colours can be used i.e. `&c`.

`-s`, `--slot`

The slot to put the objective into. Defaults to `PLAYER_LIST` if not provided. Available slots:
`[BELOW_NAME, PLAYER_LIST, SIDEBAR]`

`-p`, `--percent`

Use the objective from the [PercentHealth module](../modules/PercentHealth.md). If the module is not
loaded this will show an error instead. `-n` has no effect when using this flag.

## Examples

`/showhealth`

Shows 0-20 vanilla scoreboard health in the player list

`/showhealth -d "&cPlayer Health" -s SIDEBAR`

Shows 0-20 vanilla scoreboard health in the sidebar with the coloured display name 'Player Health'

`/showhealth -p -s BELOW_NAME`

Shows 0-100 percent health below the name with the display name 'Health'

## Permissions

`uhc.command.showhealth` - allows use of the command, default OP