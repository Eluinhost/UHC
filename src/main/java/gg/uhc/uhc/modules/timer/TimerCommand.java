/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.timer.TimerCommand
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

package gg.uhc.uhc.modules.timer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import gg.uhc.flagcommands.joptsimple.ArgumentAcceptingOptionSpec;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.FixedValuesTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import gg.uhc.uhc.modules.timer.messages.TemplatedMessage;
import gg.uhc.uhc.modules.timer.messages.TimerMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TimerCommand extends TemplatedOptionCommand {

    protected final TimerModule timer;

    protected final OptionSpec<String> messageSpec;
    protected final OptionSpec<Void> cancelSpec;
    protected final ArgumentAcceptingOptionSpec<Long> timeSpec;

    protected TimerMessage lastMessage = null;

    public TimerCommand(MessageTemplates messages, TimerModule timer) {
        super(messages);
        this.timer = timer;

        this.cancelSpec = parser
                .acceptsAll(ImmutableList.of("c", "cancel"), "Cancels the current timer");

        this.timeSpec = parser
                .acceptsAll(ImmutableList.of("t", "time"), "The amount of time to run the timer for")
                .requiredUnless(cancelSpec)
                .withRequiredArg()
                .withValuesConvertedBy(new TimeConverter());
        completers.put(timeSpec, new FixedValuesTabComplete("10m", "30m", "1h", "90m"));

        this.messageSpec = parser.nonOptions("Message to show on the timer, if none is supplied then the last message sent is used instead");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        boolean running = timer.isRunning();

        if (options.has(cancelSpec)) {
            if (!running) {
                sender.sendMessage(messages.getRaw("none running"));
                return true;
            }

            timer.cancel();
            sender.sendMessage(messages.getRaw("cancelled"));
            return true;
        }

        long time = timeSpec.value(options);
        List<String> messageParts = messageSpec.values(options);

        TimerMessage message;
        if (messageParts.size() == 0) {
            if (lastMessage == null) {
                sender.sendMessage(messages.getRaw("none previous"));
                return true;
            }

            message = lastMessage;
        } else {
            String actualMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(messageParts));
            lastMessage = message = new TemplatedMessage(messages.getTemplate("format"), actualMessage);
        }

        if (running) {
            timer.cancel();
        }

        timer.startTimer(message, time);
        sender.sendMessage(messages.getRaw("started"));
        return true;
    }
}
