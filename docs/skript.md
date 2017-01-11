## Events

### On module change status

Trigger on module enable/disable/toggles

#### Pattern
 
`module[s] ((change state|toggle[d])|enable[d]|disable[d]`

#### Examples:

`on module toggled` - Called whenever any module is toggled

`on modules enabled` - Called whenever any module is enabled

## Expressions

### Module

Get a module by its name

#### Pattern

`[the] module[s] called %strings%`

## Conditions

### Check enabled/disabled

Checks if all of the provided modules are enabled or not

#### Pattern

`if %modules?% (is|are) (enabled|disabled)`

#### Examples

`if the module "PVP" is enabled:`

`if all modules are disabled:`