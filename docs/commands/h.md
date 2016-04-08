# `/h`

Shows/Modifies the health of each player specified. If no players are specified shows/modifies your health instead.

`-a` - Show/modify the health of all online players
`-c`/`-current` - Instead of showing player healths, set their current health instead. Requires permission `uhc.command.health.modify`.
`-m`/`-maximum` - Instead of showing player healths, set their maximum health instead. Requires permission `uhc.command.health.modify`.
`-s`/`-silent` - Do not send feedback to players whose health was modified

## Examples

`/h ghowden` - Show health of player named "ghowden"
`/h -a` - Show all online player's health
`/h -am 40` - Set maximum health of all players to 40 (20 hearts)
`/h -sc 60 ghowden Eluinhost` - Set health of players named "ghowden" and "Eluinhost" to 60 (30 hearts) without sending feedback

## Permissions

`uhc.command.health` - allows use of the command, default true  
`uhc.command.health.modify` - allows usage of `-s` and `-m`, default op
