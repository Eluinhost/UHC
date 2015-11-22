/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.requests.TeamRequestCommand
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

import com.google.common.collect.ImmutableSet;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.StringConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.StringPredicates;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamRequestCommand extends OptionCommand {

    protected final MessageTemplates messages;
    protected final RequestManager requests;

    protected OptionSpec<String> playerNameSpec;

    public TeamRequestCommand(MessageTemplates messages, RequestManager requests) {
        this.messages = messages;
        this.requests = requests;

        playerNameSpec = parser.nonOptions("Player names to create a team with")
                .withValuesConvertedBy(new StringConverter().setPredicate(new StringPredicates.LessThanOrEqualLength(16)).setType("Player name"));
        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messages.getRaw("as player"));
            return true;
        }

        TeamRequest request = new TeamRequest(((Player) sender).getUniqueId(), ImmutableSet.copyOf(playerNameSpec.values(options)));
        requests.addRequest(request);

        return true;
    }
}
