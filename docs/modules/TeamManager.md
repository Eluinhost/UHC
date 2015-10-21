TeamManager
===========

The icon for this module is blue wool. This module is not disableable.

This module creates a number of 'UHC teams' on startup based on the configuration.

There are [multiple commands](../commands/teams/TeamCommands.md) that can be used to modify teams and require this 
module to be loaded to work.

### Configuration

```yaml
removed team combos:
- RESET
- STRIKETHROUGH
- MAGIC
- BLACK
- WHITE
- =GRAY+ITALIC
```

The module creates every possible combination of team based on:

` 1 colour + combination of formatting codes `

Combinations in `removed team combos` are removed from the teams created.

Codes are separated by `+`.

If a line starts with `=` it only stops that exact combination from occuring. 
Otherwise it will stop any team containing the codes.

E.g.

`STRIKETHROUGH` will stop both `WHITE, STRIKETHOUGH` and `BLACK, STRIKETHOUGH`

`=GRAY+ITALIC` will stop `GRAY, ITALIC` but not `GRAY, ITALIC, UNDERLINE`

`WHITE+STRIKETHOUGH` will stop `WHITE, STRIKETHROUGH` and `WHITE, STRIKETHOUGH, ITALIC` but not `WHITE, ITALIC` or `BLACK, STRIKETHROUGH`