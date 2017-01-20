## Events

 - On module change status
   - Description: Trigger on module enable/disable/toggles
   - Pattern: `module[s] ((change state|toggle[d])|enable[d]|disable[d])`
   - Examples:
     - `on module toggled` - Called whenever any module is toggled
     - `on modules enabled` - Called whenever any module is enabled

## Expressions

- Get module by name
  - Pattern: `[the] module[s] called %strings%`
  
- Get module's name
  - Patterns: 
    - `[the] name[s] of %modules%"`
    - `%modules%'[s] name`
  
- Get module's enabled status (boolean)
  - Patterns: 
    - `[the] [enabled] status[es] of %modules%"`
    - `"%modules%'[s] [enabled] status[es]`
  
- Get golden head heal amount
  - Patterns: 
    - `[the] heal amount of %module%`
    - `"%module%'[s] heal amount`
  
## Conditions

- Check module status
  - Description: Checks if all of the modules requested are enabled/disabled
  - Pattern: `if %modules?% (is|are) (enabled|disabled)`
  - Examples:
    - `if the module "PVP" is enabled:`
    - `if all modules are disabled:`
    
## Effects

- Change status of module
  - Description: Enables/Disables/Toggles the request modules
  - Pattern: `((change state|toggle[d])|enable[d]|disable[d]) %modules%`
  - Examples:
    - `disable all modules`
    - `enable the module called "PvP"`
    - `disable the modules called "GoldenHeads", "ExtendedSaturation" and "ChatHealth"`
    
