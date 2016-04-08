Timer
=====

This module will not load unless ProtocolLib is installed and is not disableable. 

The icon for this module is clock.

For documentation on the timer command visit the [/timer documentation](../commands/timer.md)

At least 1 of 'boss bar' or 'action bar' must be used for this module to load.
If neither are enabled this module will fail to load, if both are loaded then
the boss bar takes precendence. 

This module requires either 1.9+ (boss bar) or ProtocolLib (action bar) to run

### Configuration

```yaml
use boss bar: true
boss bar colour: BLUE
boss bar style: SOLID
use action bar: true
```

`use boss bar` Whether to use the boss bar for the timer, requires 1.9+

`boss bar colour` The colour for the boss bar, values can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarColor.html)

`boss bar style` The style of the boss bar, values can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarStyle.html)

`use action bar` Whether to use the action bar for the timer, requires ProtocolLib


