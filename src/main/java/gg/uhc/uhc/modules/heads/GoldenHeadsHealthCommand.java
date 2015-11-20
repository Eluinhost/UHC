/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.heads.GoldenHeadsHealthCommand
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

package gg.uhc.uhc.modules.heads;

import com.google.common.collect.ImmutableMap;
import gg.uhc.flagcommands.converters.IntegerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.IntegerPredicates;
import gg.uhc.flagcommands.tab.FixedValuesTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GoldenHeadsHealthCommand extends TemplatedOptionCommand {

    protected final GoldenHeadsModule module;

    protected final OptionSpec<Integer> spec;
    protected final OptionSpec<Void> silentSpec;

    public GoldenHeadsHealthCommand(MessageTemplates messages, GoldenHeadsModule module) {
        super(messages);
        this.module = module;

        spec = parser.nonOptions("How much HP points to heal total with a golden apple")
                .withValuesConvertedBy(new IntegerConverter().setPredicate(IntegerPredicates.GREATER_THAN_ZERO).setType("Integer > 0"));
        nonOptionsTabComplete = new FixedValuesTabComplete("4", "5", "6", "7", "8");

        silentSpec = parser.accepts("s", "Sends the response only to you and not the entire server");
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        List<Integer> healths = spec.values(options);

        if (healths.size() == 0) {
            sender.sendMessage(messages.getRaw("provide number"));
            return true;
        }

        module.setHealAmount(healths.get(0));

        boolean silent = options.has(silentSpec);

        String response = messages.evalTemplate(silent ? "silent" : "response", ImmutableMap.of("amount", module.getHealAmount()));

        if (options.has(silentSpec)) {
            sender.sendMessage(response);
        } else {
            Bukkit.broadcastMessage(response);
        }

        return true;
    }
}
