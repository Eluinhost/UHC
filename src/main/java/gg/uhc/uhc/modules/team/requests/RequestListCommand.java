/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.requests.RequestListCommand
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

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RequestListCommand implements CommandExecutor {

    protected static final String LINE_FORMAT = ChatColor.DARK_GRAY + "ID %d from '%s' to team with: " + ChatColor.DARK_PURPLE + "%s ";
    protected static final String NO_REQUESTS = ChatColor.AQUA + "There are curently no team requests waiting";

    protected final RequestManager requestManager;

    public RequestListCommand(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<TeamRequest> requests = requestManager.getRequests();

        if (requests.size() == 0) {
            sender.sendMessage(NO_REQUESTS);
        } else {
            for (TeamRequest request : requests) {
                sender.sendMessage(String.format(LINE_FORMAT, request.getId(), request.getOwnerName(), Joiner.on(", ").join(request.getOthers())));
            }
        }

        return true;
    }
}
