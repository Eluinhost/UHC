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
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TimerCommand extends OptionCommand {

    protected static final String TIMER_NOT_RUNNING = ChatColor.RED + "There is no timer running currently";
    protected static final String TIMER_CANCELLED = ChatColor.AQUA + "Timer cancelled";
    protected static final String LAST_MESSAGE_NOT_AVAILABLE = ChatColor.RED + "You must provide a message as there was no previous timer set";
    protected static final String TIMER_STARTED = ChatColor.AQUA + "Timer started";

    protected final TimerModule timer;

    protected final OptionSpec<String> messageSpec;
    protected final OptionSpec<Void> cancelSpec;
    protected final OptionSpec<Long> timeSpec;

    protected TimerMessage lastMessage = null;

    public TimerCommand(TimerModule timer) {
        this.timer = timer;

        this.cancelSpec = parser
                .acceptsAll(ImmutableList.of("c", "cancel"), "Cancels the current timer");

        this.timeSpec = parser
                .acceptsAll(ImmutableList.of("t", "time"), "The amount of time to run the timer for")
                .requiredUnless(cancelSpec)
                .withRequiredArg()
                .withValuesConvertedBy(new TimeConverter());

        this.messageSpec = parser.nonOptions("Message to show on the timer, if none is supplied then the last message sent is used instead");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        boolean running = timer.isRunning();

        if (options.has(cancelSpec)) {
            if (!running) {
                sender.sendMessage(TIMER_NOT_RUNNING);
                return true;
            }

            timer.cancel();
            sender.sendMessage(TIMER_CANCELLED);
            return true;
        }

        long time = timeSpec.value(options);
        List<String> messageParts = messageSpec.values(options);

        TimerMessage message;
        if (messageParts.size() == 0) {
            if (lastMessage == null) {
                sender.sendMessage(LAST_MESSAGE_NOT_AVAILABLE);
                return true;
            }

            message = lastMessage;
        } else {
            lastMessage = message = new TimeAppendedMessage(ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(messageParts)));
        }

        if (running) {
            timer.cancel();
        }

        timer.startTimer(message, time);
        sender.sendMessage(TIMER_STARTED);
        return true;
    }
}
