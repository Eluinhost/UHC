Message Modification
====================

The majority of the messages sent by the plugin can be edit via a messages.conf file in the configuration directory.

When the plugin is started up the file `messages.reference.conf` is written to the folder. This is the REFERENCE copy.
By creating a `messages.conf` file in the same directory you can override any of the messages.

The file is in HOCON format and anything inside of `messages.conf` will override `messages.reference.conf`. Anything not
found in `messages.conf` will fallback to `messages.reference.conf` so you should only use it to override what you need
to.

You can check out the reference file [here](../src/main/resources/messages.reference.conf)

### Example 1

Change the 'success' colour of all commands (default is aqua). Both of the following are valid for overriding:

```
colours.command=${colours.gold}
```

```
{
  "colours" : {
    "command" : ${colours.gold}
  }
}
```

### Example 2

Changing team coords command to not include the world name and change the prefix

By checking the reference we can see the default message is 

`"format" : ${colours.secondary}"[Team Coords] {{name}}: {{world}} {{x}}:{{y}}:{{z}}"`

So we can override it like this:

```
commands.tc.format=${colours.secondary}"[TC] {{name}}: {{x}}:{{y}}:{{z}}
```