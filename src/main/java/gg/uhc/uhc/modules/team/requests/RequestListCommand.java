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
import com.google.common.collect.ImmutableMap;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class RequestListCommand implements CommandExecutor {

    protected final MessageTemplates messages;
    protected final RequestManager requestManager;

    public RequestListCommand(MessageTemplates messages, RequestManager requestManager) {
        this.messages = messages;
        this.requestManager = requestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<TeamRequest> requests = requestManager.getRequests();

        if (requests.size() == 0) {
            sender.sendMessage(messages.getRaw("no requests"));
            return true;
        }

        for (TeamRequest request : requests) {
            Map<String, Object> context = ImmutableMap.<String, Object>builder()
                    .put("id", request.getId())
                    .put("name", request.getOwnerName())
                    .put("members", Joiner.on(", ").join(request.getOthers()))
                    .build();

            sender.sendMessage(messages.evalTemplate("list request", context));
        }

        return true;
    }
}
