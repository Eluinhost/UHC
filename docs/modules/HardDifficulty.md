HardDifficulty
==============

The icon for this module is an Arrow.

For documentation on how to restrict worlds affected by this module please
see the [world whitelist documentation](WorldWhitelist.md)

### On enable:

All loaded worlds are converted to difficulty 3.

Any other worlds that are loaded whilst enabled will be forced to difficulty 3.

### On disbale:

Stops forcing worlds to difficulty 3 when they load. Does not change any world difficulties.