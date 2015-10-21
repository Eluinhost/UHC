World Whitelist
===============

Some modules have world whitelist configuration.

```yaml
worlds:
- world_name_1
- world_name_2
worlds are whitelist: false
```

`worlds`

A list of world names to use

`worlds are whitelist`

If this is true all of the worlds in `worlds` are the only worlds that will be affected.
If it is false then all of the worlds are the worlds that will *not* be affected.