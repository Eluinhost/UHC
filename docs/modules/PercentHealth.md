PercentHealth
=============

The icon for this module is a daylight sensor.

When enabled this module will update the objective with player's health 
in percent.

When disabled the modules stops updating the objective.

This module needs to be loaded for the [/showhealth command](../commands/showhealth.md) to use percentage health

### Configuration

```yaml
objective name: UHCHealthPercent
update period: 20
```

`objective name` - the name of the objetive to create/update  
`update period` - how often to update player's health (in ticks)

