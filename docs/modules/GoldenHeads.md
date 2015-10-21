GoldenHeads
===========

The icon for this module is a player head and the amount is how many
half hearts a golden head will heal.

![head](../../images/example-inventory-with-config.png)

When enabled golden heads are craftable and will heal the configured amount

When disabled golden heads are uncraftable and any existing heads are treated
as regular golden apples.

The heal amount can also be modified by using the [/ghead command](../commands/ghead.md)

### Configuration

```yaml
heal amount: 6
```

The amount of half hearts to heal