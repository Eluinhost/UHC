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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.List;

public class RequestResponseCommand implements TabExecutor {

    protected final MessageTemplates messages;
    protected final RequestManager requestManager;
    protected final RequestManager.AcceptState state;

    public RequestResponseCommand(MessageTemplates messages, RequestManager requestManager, RequestManager.AcceptState state) {
        this.messages = messages;
        this.requestManager = requestManager;
        this.state = state;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(RequestManager.ADMIN_PERMISSION)) {
            sender.sendMessage(messages.getRaw("no admin permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(messages.getRaw("accept deny usage"));
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(messages.getRaw("accept deny usage"));
            return true;
        }

        if (!requestManager.finalizeRequest(id, state)) {
            sender.sendMessage(messages.evalTemplate("request not found", ImmutableMap.of("id", id)));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return StringUtil.copyPartialMatches(args[args.length - 1], Iterables.transform(requestManager.getRequests(), GET_ID), Lists.<String>newArrayList());
    }

    protected static final Function<TeamRequest, String> GET_ID = new Function<TeamRequest, String>() {
        @Override
        public String apply(TeamRequest input) {
            return String.valueOf(input.getId());
        }
    };
}
