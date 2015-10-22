/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.requests.RequestResponseCommand
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.modules.team.requests;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RequestResponseCommand implements CommandExecutor {

    protected static final String USAGE = ChatColor.RED + "USAGE: accept|deny <request id>";
    protected static final String NONE_WITH_ID = ChatColor.RED + "No request found with the ID %d";

    protected final RequestManager requestManager;
    protected final RequestManager.AcceptState state;

    public RequestResponseCommand(RequestManager requestManager, RequestManager.AcceptState state) {
        this.requestManager = requestManager;
        this.state = state;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(RequestManager.ADMIN_PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to accept or deny requests");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(USAGE);
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(USAGE);
            return true;
        }

        if (!requestManager.finalizeRequest(id, state)) {
            sender.sendMessage(String.format(NONE_WITH_ID, id));
        }

        return true;
    }
}
