/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.health.HealthCommand
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

package gg.uhc.uhc.modules.health;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

public class HealthCommand extends OptionCommand {

    protected final NumberFormat format;
    protected final OptionSpec<Player> playerSpec;
    protected final Function<Player, String> getHealthString;

    public HealthCommand() {
        playerSpec = parser.nonOptions("List of player names to check the health of")
                .withValuesConvertedBy(new OnlinePlayerConverter());

        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);

        format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);

        getHealthString = new Function<Player, String>() {
            @Override
            public String apply(Player input) {
                return input.getName() + ": " + format.format(input.getHealth() / input.getMaxHealth() * 100D) + "%";
            }
        };
    }

    @Override
    protected boolean runCommand(CommandSender commandSender, OptionSet optionSet) {
        List<Player> toShow = playerSpec.values(optionSet);

        if (toShow.size() == 0 && commandSender instanceof Player) {
            toShow = Lists.newArrayList((Player) commandSender);
        }

        commandSender.sendMessage(ChatColor.AQUA + "Player healths (" + toShow.size() + "): " + ChatColor.DARK_PURPLE + Joiner.on(" ").join(Iterables.transform(toShow, getHealthString)));
        return true;
    }
}
