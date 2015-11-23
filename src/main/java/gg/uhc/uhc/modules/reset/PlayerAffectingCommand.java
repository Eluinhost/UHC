/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.reset.PlayerAffectingCommand
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

package gg.uhc.uhc.modules.reset;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import gg.uhc.uhc.modules.reset.actions.Action;
import gg.uhc.uhc.modules.reset.resetters.PlayerResetter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class PlayerAffectingCommand extends TemplatedOptionCommand {

    protected final PlayerResetter resetter;

    protected final OptionSpec<Player> playersSpec;
    protected final OptionSpec<Void> allPlayerSpec;
    protected final OptionSpec<Void> undoSpec;

    public PlayerAffectingCommand(MessageTemplates messages, PlayerResetter resetter) {
        super(messages);
        this.resetter = resetter;

        playersSpec = parser.nonOptions("List of online players to affect, leave empty to only just on yourself")
                .withValuesConvertedBy(new OnlinePlayerConverter());
        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);

        allPlayerSpec = parser.acceptsAll(ImmutableSet.of("a", "all"), "Affect all online players");

        undoSpec = parser.acceptsAll(ImmutableSet.of("u", "undo"), "Undo the last command you ran (within ~" + (int) (resetter.getCacheTicks() / 20) + " seconds of running it)");
    }

    // override to show a message when ran with *
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "*".equals(args[0])) {
            sender.sendMessage(messages.getRaw("dont use star"));
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        if (options.has(undoSpec)) {
            List<Action> actions = resetter.getLastActions(sender.getName());

            if (actions.size() == 0) {
                sender.sendMessage(messages.getRaw("nothing to undo"));
                return true;
            }

            int reverted = 0;
            for (Action revertable : actions) {
                if (revertable.revert()) reverted++;
            }

            sender.sendMessage(messages.evalTemplate("undone", ImmutableMap.of("count", reverted, "total", actions.size())));
            return true;
        }

        Collection<Player> players;
        if (options.has(allPlayerSpec)) {
            players = Lists.newArrayList(Bukkit.getOnlinePlayers());
        } else {
            players = playersSpec.values(options);

            // check if none are provided and run for just the player
            if (players.size() == 0) {
                if (sender instanceof Player) {
                    players = Lists.newArrayList((Player) sender);
                } else {
                    sender.sendMessage(messages.getRaw("players only"));
                    return true;
                }
            }
        }

        List<Action> toRun = resetter.createActions(sender.getName(), players);

        int affected = 0;
        for (Action action : toRun) {
            if (action.run()) affected++;
        }

        for (Player player : players) {
            player.sendMessage(messages.getRaw("for player"));
        }

        sender.sendMessage(messages.evalTemplate("for sender", ImmutableMap.of("count", affected, "total", toRun.size())));
        sender.sendMessage(messages.evalTemplate("notice", ImmutableMap.of("cache", (int) (resetter.getCacheTicks() / 20))));
        return true;
    }
}
