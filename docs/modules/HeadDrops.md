HeadDrops
=========

The icon for this module is a player head.

When enabled and a player dies:

- The dead player gets 18 ticks invisibility
- An armour stand is created in their place that is invincible for 2 seconds
- The armour stand takes the player's velocity + a little bit more
- The armour stand takes the player's chestplate, leggings, boots and item in hand
- The armour stand wears the player's head

When disabled nothing happens

### Configuration

```yaml
drop chance: 100
```

The change for the effect to happen in %