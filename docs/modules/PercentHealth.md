PercentHealth
=============

The icon for this module is a daylight sensor.

When enabled this module will update all of the configured objectives with player's health
in percentages.

When disabled the modules stops updating the objectives.

### Configuration

```yaml
update period: 20
objectives:
- objective name: UHCHealthName
  objective display name: '&c&h'
- objective name: UHCHealthList
  objective display name: Health
  scaling: 5
```

`update period` - how often to update player's health (in ticks)  
`objectives` - a list of objectives to create/track  
	`objective name` - the name of the objetive to create/update  
	`objective display name` - the display name to give the objective, can use colour codes like `&c`. Using `&h` will be replaced with a heart.  
  `scaling` - int, how much to multiply the player's health by. Defaults to `5` if not provided (changes 0-20 into 0-100)  
