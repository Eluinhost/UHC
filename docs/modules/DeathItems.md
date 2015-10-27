DeathItems
==========

The icon for this module is a bucket.

When enabled the configured items will be added to the player's drops when they die

## Configuration

```yaml
items:
  example wool:
    amount: 3
    type: WOOL
    data: 4
  example dirt:
    amount: 2
    type: DIRT
```

Each item (`example wool`/`example dirt`) should have a unique name

`amount` - how many to drop

`type` - the [Material name](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) to drop

`data` - optional, if provided sets the durability value of the item