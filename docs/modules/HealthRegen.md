HealthRegen
===========

The icon for this module is water potion when enabled and a
regeneration potion when enabled.

For documentation on how to restrict worlds affected by this module please
see the [world whitelist documentation](WorldWhitelist.md)

### On enable:

All loaded worlds have gamerule `doNaturalRegeneration` set to true.

Any other worlds that are loaded whilst enabled have `doNaturalRegeneration` set to true.

### On disable:

All loaded worlds have gamerule `doNaturalRegeneration` set to false.

Any other worlds that are loaded whilst disabled have `doNaturalRegeneration` set to false.