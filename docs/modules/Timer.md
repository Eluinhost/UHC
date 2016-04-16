Timer
=====

This module will not load unless ProtocolLib is installed and is not disableable. 

The icon for this module is clock.

For documentation on the timer command visit the [/timer documentation](../commands/timer.md)

At least 1 of 'boss bar' or 'action bar' or 'tab list' must be used for this module to load.
If none are enabled this module will fail to load, if all are loaded then
they are use in this order:
 
BOSS BAR > TAB LIST > ACTION BAR

BOSS BAR requires Minecraft 1.9+

TAB LIST and ACTION BAR both require ProtocolLib to also be installed


### Configuration

```yaml
use boss bar: true
boss bar colour: BLUE
boss bar style: SOLID
use tab list: true
tab list position: BOTTOM
use action bar: true
```

`use boss bar` Whether to use the boss bar for the timer, requires 1.9+

`boss bar colour` The colour for the boss bar, values can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarColor.html)

`boss bar style` The style of the boss bar, values can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarStyle.html)

`use tab list` Whether to use the tab list for the timer, requires ProtocolLib

`tab list position` Either TOP or BOTTOM, what end of the tab list to render on

`use action bar` Whether to use the action bar for the timer, requires ProtocolLib


