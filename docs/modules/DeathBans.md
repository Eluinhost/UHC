DeathBans
=========

The icon for this module is a barrier.

When disabled this module does nothing on player death.

## Configuration

This module requires the following configuration regardless of actions chosen:

```yaml
action: BAN+KICK
message: RIP
delay seconds: 20
```

`action` - the actions to take, separated by `+` look below for more information  
`message` - the message to send to the player after the delay
`delay seconds` - how many seconds after death to run actions

If a player logs out during the delay the actions are ran immediately

### Actions

#### `BAN`

Adds a ban entry for the player on the server.

Requires an extra configuration field:

```yaml
duration: 1d
```

This is how long to ban the player for (configuration for 1 day)

The ban reason will be the configured message.

NOTE: Banning a player does not remove them from the server, use another action to do this.

#### `KICK`

Kicks the player from the server with the configured message.

#### `MOVE_WORLD`

This action applies immediately after death and ignores the delay seconds.

This action sets the respawn location of the player to the specified world

Requires an extra configuraiton field:

```yaml
world name: world
```

This is the name of the world to set their respawn location to.

### `MOVE_SERVER`

This action will move the player to another server on the BungeeCord instance.

Requires an extra configuration field:

```yaml
server name: lobby
```

This is the name of the BungeeCord server to move the player into.