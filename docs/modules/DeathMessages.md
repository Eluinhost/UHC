DeathMessages
=============

The icon for this module is a banner.

When enabled, modifies any death messages that occur. When disabled does nothing.

## Configuration

```yaml
format: "&c{{original}} at {{player.world}},{{player.blockCoords}}"
format explanation: "<message> at <coords>"
```

`format` - the format to show the death messages in, check below for more information. Can use colour codes like `&c`.
If the format creates an empty string then no message will be sent on death.

`format explanation` - this is what is shown in the `/uhc` inventory as the format. Change this to something
simple that explains your formatting.

## Formatting

Uses [Mustache formatting](https://mustache.github.io/mustache.5.html).

Available variables:

		'original' - the original death message
		
		'hasKiller' - true if the player was killed by another
		
		'killer' - same as 'player' below, only exists if 'hasKiller' is true
		
		'player' - the object of the player that died
		
			'player.name' - the player's name
			
			'player.displayName' - the display name of the player
			
			'player.health` - the player's health
			
			'player.maxHealth' - the player's max health
			
			'player.percentHealth' - the player's health in percent
		
			'player.world' - the name of the world
			
			'player.environment' - the world environment (NORMAL/NETHER/THE_END)
			
			'player.blockCoords' - x,y,z
			
			'player.rawCoords' - the player's location as a Vector
			
				'player.rawCoords.x' - the exact x coord
			
				'player.rawCoords.y' - the exact y coord
			
				'player.rawCoords.z' - the exact z coord    

## Examples

#### Just show coords on death (no world name)

`format: "Someone died at {{player.blockCoords}}"`

#### Only show PVP deaths with only the killers name and health

`format: "{{#hasKiller}}{{killer.name}} killed someone. They are now at {{killer.percentHealth}}%{{/hasKiller}}"`

#### Add coords to original message

`format: "{{original}} {{player.world}},{{player.blockCoords}}"`

#### Suppress death messages

`format: ""`