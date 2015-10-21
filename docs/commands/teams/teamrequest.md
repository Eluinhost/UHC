# `/teamrequest`

## `/teamrequest request`

Adds a request to team with the given players.

`/teamrequest request Eluinhost`

This would show a message to all admins (with permission `uhc.command.teamrequestadmin`):

`New request (ID: 2) from ghowden to team with: Eluinhost ACCEPT | DENY`

Admins can then click on ACCEPT or DENY to approve/reject the request or run the commands
`/teamrequest accept|deny <id>`

Requests are automatically cancelled after 2 minutes with no answer.

If rerequested within the timeout period the old request is cancelled and replaced with the new request

Permission: `uhc.command.teamrequest` default true

## `/teamrequest accept|deny <id>`

Approve/Reject the request with the given ID

If approved the player and any others they specified are put into an empty UHC together

If rejected nothing happens except for a rejection message 

Permission: `uhc.command.teamrequestadmin` default OP

## `/teamrequest list`

List all pending requests

Permission: `uhc.command.teamrequestadmin` default OP