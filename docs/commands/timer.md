# `/timer`

Starts a timer which is displayed above the action bar for all online players.

This command requires ProtocolLib to be installed and that the [Timer module](../modules/Timer.md)
is loaded.

`-c` - cancels the current timer

`-t` - the amount of time to set the timer for e.g. `1h30m`, `1 day 20 seconds`

`other arguments` - the message to send. If none supplied tries to use the message from the
last timer set. Can include colour codes like `&c` e.t.c.

## Examples

`/timer -c` - cancels the current timer if one exists

`/timer -t 1h30m Meetup` - starts a timer for 1 hour and 30 minutes with the message 'Meetup'

`/timer -t "1 day 2 hours" Go to 0,0` - starts a timer for 1 day and 2 hours with the message 'Go to 0,0'

## Permissions

`uhc.command.timer` - allows use of the command, default OP