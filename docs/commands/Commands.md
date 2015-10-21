Commands
========

Any command that accepts flags can have their help shown by using `-?`, `-h` or `--help` 

e.g. `/timer -?`

Commands that support flags support quoted arguments.

e.g. `/timer -t "1 day 2 seconds" message` 

will set "1 day 2 seconds" to the `t` flag whereas if you miss off the quotes 

`/timer -t 1 day 2 seconds message` 

it will only take "1" and the rest will be assumed to belong to the message. 

Quotes are optional for single word arguments like `/timer -t 1d2s message`

