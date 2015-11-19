Player Reset Commands
=====================

All of the following commands work the same.

`/command player1 player2` runs the command for each supplied player

`/command` runs the command for just yourself

`/command -a` runs for all online players

`/command -u` undoes the last invocation of the command from yourself (does not undo other peoples commands). All of the
command allow an undo for up to 30 seconds after running the command.

## `/heal`

Heals player to full health.

Permission: `uhc.command.heal` default OP

## `/feed`

Feeds player to their max hunger, maxes their saturation and removes their exhaustion.

Permission: `uhc.command.heal` default OP

## `/clearxp`

Removes all XP from the player

Permission: `uhc.command.clearxp` default OP

## `/ci`

Clears the inventory, armour slots, crafting inventory and cursor item of the player

Permission: `uhc.command.ci` default OP

## `/cleareffects`

Removes all potion effects from the player

Permission: `uhc.command.cleareffects` default OP

## `/reset`

Has the effect of all of the above commands in one.

Permission: `uhc.command.reset` default OP