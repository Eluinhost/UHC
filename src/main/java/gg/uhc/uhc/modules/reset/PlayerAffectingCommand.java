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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.commands.OptionCommand;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class PlayerAffectingCommand extends OptionCommand {

    protected final PlayerResetter resetter;
    protected final OptionSpec<Player> playersSpec;
    protected final OptionSpec<Void> allPlayerSpec;

    public PlayerAffectingCommand(PlayerResetter resetter) {
        this.resetter = resetter;

        playersSpec = parser.nonOptions("List of online players to affect, leave empty to only just on yourself")
                .withValuesConvertedBy(new OnlinePlayerConverter());
        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);

        allPlayerSpec = parser.acceptsAll(ImmutableSet.of("a", "all"), "Affect all online players");
    }

    public abstract Optional<String> affectPlayers(Collection<? extends Player> players);

    // override to show a message when ran with *
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "*".equals(args[0])) {
            sender.sendMessage(ChatColor.RED + "Usage of * is no longer allowed, please use -a instead");
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    protected boolean runCommand(CommandSender sender, OptionSet options) {
        Collection<? extends Player> players;

        if (options.has(allPlayerSpec)) {
            players = Bukkit.getOnlinePlayers();
        } else {
            players = playersSpec.values(options);

            // check if none are provided and run for just the player
            if (players.size() == 0) {
                if (sender instanceof Player) {
                    players = Lists.newArrayList((Player) sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "You can only run this command without arguments as a player");
                    return true;
                }
            }
        }

        Optional<String> message = affectPlayers(players);

        if (message.isPresent()) {
            sender.sendMessage(message.get());
        }

        return true;
    }
}
