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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import gg.uhc.flagcommands.converters.DoubleConverter;
import gg.uhc.flagcommands.converters.OnlinePlayerConverter;
import gg.uhc.flagcommands.joptsimple.OptionSet;
import gg.uhc.flagcommands.joptsimple.OptionSpec;
import gg.uhc.flagcommands.predicates.DoublePredicates;
import gg.uhc.flagcommands.tab.NonDuplicateTabComplete;
import gg.uhc.flagcommands.tab.OnlinePlayerTabComplete;
import gg.uhc.uhc.commands.TemplatedOptionCommand;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

public class HealthCommand extends TemplatedOptionCommand {

    protected static final NumberFormat FORMAT = NumberFormat.getInstance();
    protected static final String MODIFY_PERMISSION = "uhc.command.health.modify";

    static {
        FORMAT.setMaximumFractionDigits(2);
        FORMAT.setMinimumFractionDigits(0);
    }

    protected final OptionSpec<Player> playerSpec;
    protected final OptionSpec<Void> allPlayersSpec;
    protected final OptionSpec<Void> silentSpec;
    protected final OptionSpec<Double> setCurrentHealthSpec;
    protected final OptionSpec<Double> setMaximumHealthSpec;
    protected final double maximumHealthWarningLimit;

    public HealthCommand(MessageTemplates messages, double maximumHealthWarningLimit) {
        super(messages);
        this.maximumHealthWarningLimit = maximumHealthWarningLimit;

        playerSpec = parser.nonOptions("List of player names to check/modify the health of")
                .withValuesConvertedBy(new OnlinePlayerConverter());

        nonOptionsTabComplete = new NonDuplicateTabComplete(OnlinePlayerTabComplete.INSTANCE);

        allPlayersSpec = parser
                .acceptsAll(ImmutableList.of("a", "all"), "Check/modify all online players");
        silentSpec = parser
                .acceptsAll(ImmutableList.of("s", "silent"), "Do not notify players about their health being changed");

        setCurrentHealthSpec = parser
                .acceptsAll(ImmutableList.of("c", "current"), "Set the health of players")
                .withRequiredArg()
                .withValuesConvertedBy(new DoubleConverter()
                        .setPredicate(DoublePredicates.GREATER_THAN_ZERO)
                        .setType("New player health")
                );

        setMaximumHealthSpec = parser
                .acceptsAll(ImmutableList.of("m", "maximum"), "Set the maximum health of players")
                .withRequiredArg()
                .withValuesConvertedBy(new DoubleConverter()
                        .setPredicate(DoublePredicates.GREATER_THAN_ZERO)
                        .setType("New maximum player health")
                );
    }

    @Override
    protected boolean runCommand(CommandSender commandSender, OptionSet optionSet) {
        List<Player> players = playerSpec.values(optionSet);

        if (optionSet.has(allPlayersSpec)) {
            players = Lists.newArrayList(Bukkit.getOnlinePlayers());
        }

        if (players.size() == 0) {
            if (commandSender instanceof Player) {
                players = Lists.newArrayList((Player) commandSender);
            } else {
                commandSender.sendMessage(messages.getRaw("no players"));
                return true;
            }
        }

        boolean isSettingCurrentHealth = optionSet.has(setCurrentHealthSpec);
        boolean isSettingMaximumHealth = optionSet.has(setMaximumHealthSpec);
        boolean isModification = isSettingCurrentHealth || isSettingMaximumHealth;

        if (isModification && !commandSender.hasPermission(MODIFY_PERMISSION)) {
            commandSender.sendMessage(messages.getRaw("cannot modify"));
            return true;
        }

        // Set maximum health before current health in case of multiple operation
        if (isSettingMaximumHealth) {
            double newMaximumHealth = setMaximumHealthSpec.value(optionSet);

            boolean limitedByServer = false;

            for (Player player : players) {
                player.setMaxHealth(newMaximumHealth);
                double actualMaxHealth = player.getMaxHealth();
                if (!limitedByServer && actualMaxHealth != newMaximumHealth) {
                    limitedByServer = true;

                    ImmutableMap<String, String> context = ImmutableMap.of(
                            "attempted", FORMAT.format(newMaximumHealth),
                            "limit", FORMAT.format(actualMaxHealth)
                    );

                    commandSender.sendMessage(messages.evalTemplate("limited by server", context));
                }
            }

            if (newMaximumHealth > maximumHealthWarningLimit) {
                ImmutableMap<String, String> context = ImmutableMap.of(
                        "attempted", FORMAT.format(newMaximumHealth),
                        "limit", FORMAT.format(maximumHealthWarningLimit)
                );

                commandSender.sendMessage(messages.evalTemplate("very high maximum health", context));
            }
        }

        if (isSettingCurrentHealth) {
            double newCurrentHealth = setCurrentHealthSpec.value(optionSet);
            for (Player player : players) {
                double playerMaxHealth = player.getMaxHealth();
                // Spigot 1.9+ throws an error trying to set a player's health higher than his maximum health
                player.setHealth(Math.min(playerMaxHealth, newCurrentHealth));
            }
        }

        String playerPart = Joiner.on(messages.getRaw("player separator")).join(Iterables.transform(players, convert));

        String template = isModification ? "modify message" : "message";
        commandSender.sendMessage(messages.evalTemplate(template, ImmutableMap.of("total", players.size(), "players", playerPart)));

        if (isModification && !optionSet.has(silentSpec)) {
            for (Player player : players) {
                if (player.equals(commandSender)) {
                    continue;
                }

                PlayerContext context = new PlayerContext(player);
                player.sendMessage(messages.evalTemplate("modify feedback", context));
            }
        }

        return true;
    }

    protected static class PlayerContext {
        private final Player player;

        public PlayerContext(Player player) {
            this.player = player;
        }

        public String name() {
            return player.getName();
        }

        public String health() {
            return FORMAT.format(player.getHealth());
        }

        public String maxHealth() {
            return FORMAT.format(player.getMaxHealth());
        }

        public String percentage() {
            return FORMAT.format(player.getHealth() / player.getMaxHealth() * 100D);
        }
    }

    protected Function<Player, String> convert = new Function<Player, String>() {
        @Override
        public String apply(Player input) {
            return messages.evalTemplate("player", new PlayerContext(input));
        }
    };
}
