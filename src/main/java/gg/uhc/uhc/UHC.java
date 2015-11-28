/*
 * Project: UHC
 * Class: gg.uhc.uhc.UHC
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

package gg.uhc.uhc;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigResolveOptions;
import gg.uhc.flagcommands.commands.SubcommandCommand;
import gg.uhc.uhc.messages.BaseMessageTemplates;
import gg.uhc.uhc.messages.MessageTemplates;
import gg.uhc.uhc.messages.SubsectionMessageTemplates;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.autorespawn.AutoRespawnModule;
import gg.uhc.uhc.modules.border.WorldBorderCommand;
import gg.uhc.uhc.modules.commands.DummyCommandFactory;
import gg.uhc.uhc.modules.commands.ModuleCommands;
import gg.uhc.uhc.modules.death.*;
import gg.uhc.uhc.modules.difficulty.DifficultyModule;
import gg.uhc.uhc.modules.enderpearls.EnderpearlsModule;
import gg.uhc.uhc.modules.food.ExtendedSaturationModule;
import gg.uhc.uhc.modules.heads.GoldenHeadsHealthCommand;
import gg.uhc.uhc.modules.heads.GoldenHeadsModule;
import gg.uhc.uhc.modules.heads.HeadDropsModule;
import gg.uhc.uhc.modules.heads.PlayerHeadProvider;
import gg.uhc.uhc.modules.health.*;
import gg.uhc.uhc.modules.horses.HorseArmourModule;
import gg.uhc.uhc.modules.horses.HorsesModule;
import gg.uhc.uhc.modules.portals.EndModule;
import gg.uhc.uhc.modules.portals.NetherModule;
import gg.uhc.uhc.modules.potions.*;
import gg.uhc.uhc.modules.pvp.GlobalPVPModule;
import gg.uhc.uhc.modules.recipes.GlisteringMelonRecipeModule;
import gg.uhc.uhc.modules.recipes.GoldenCarrotRecipeModule;
import gg.uhc.uhc.modules.recipes.NotchApplesModule;
import gg.uhc.uhc.modules.reset.PlayerAffectingCommand;
import gg.uhc.uhc.modules.reset.resetters.*;
import gg.uhc.uhc.modules.team.*;
import gg.uhc.uhc.modules.team.requests.RequestListCommand;
import gg.uhc.uhc.modules.team.requests.RequestManager;
import gg.uhc.uhc.modules.team.requests.RequestResponseCommand;
import gg.uhc.uhc.modules.team.requests.TeamRequestCommand;
import gg.uhc.uhc.modules.teleport.TeleportCommand;
import gg.uhc.uhc.modules.timer.TimerCommand;
import gg.uhc.uhc.modules.timer.TimerModule;
import gg.uhc.uhc.modules.whitelist.WhitelistClearCommand;
import gg.uhc.uhc.modules.whitelist.WhitelistOnlineCommand;
import gg.uhc.uhc.modules.xp.NerfQuartzXPModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.io.IOException;

public class UHC extends JavaPlugin {

    protected ModuleRegistry registry;
    protected DebouncedRunnable configSaver;

    protected MessageTemplates baseMessages;
    protected MessageTemplates baseCommandMessages;

    @Override
    public void onEnable() {
        // setup to save the config with a debounce of 2 seconds
        configSaver = new DebouncedRunnable(this, new Runnable() {
            @Override
            public void run() {
                saveConfigNow();
            }
        }, 40);

        FileConfiguration configuration = getConfig();

        try {
            baseMessages = new BaseMessageTemplates(setupMessagesConfig());
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Failed to load the messages configuration file, cannot start the plugin");
            setEnabled(false);
            return;
        }

        baseCommandMessages = new SubsectionMessageTemplates(baseMessages, "commands");
        DummyCommandFactory dummyCommands = new DummyCommandFactory(baseCommandMessages);

        registry = new ModuleRegistry(this, baseMessages, configuration);

        registry.register(new DifficultyModule());
        registry.register(new HealthRegenerationModule());
        registry.register(new GhastTearDropsModule());
        registry.register(new GoldenCarrotRecipeModule());
        registry.register(new GlisteringMelonRecipeModule());
        registry.register(new NotchApplesModule());
        registry.register(new AbsorptionModule());
        registry.register(new ExtendedSaturationModule());
        registry.register(new GlobalPVPModule());
        registry.register(new EnderpearlsModule());
        registry.register(new WitchesModule());
        registry.register(new NetherModule());
        registry.register(new EndModule());
        registry.register(new DeathBansModule());
        registry.register(new HorsesModule());
        registry.register(new HorseArmourModule());
        registry.register(new DeathLightningModule());
        registry.register(new ModifiedDeathMessagesModule());
        registry.register(new DeathItemsModule());
        registry.register(new ChatHealthPrependModule());
        registry.register(new NerfQuartzXPModule());

        AutoRespawnModule respawnModule = new AutoRespawnModule();
        boolean respawnModuleLoaded = registry.register(respawnModule);

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            TimerModule timer = new TimerModule();

            setup(registry.register(timer) ? new TimerCommand(forCommand("timer"), timer) : dummyCommands.forModule(timer), "timer");

            if (respawnModuleLoaded) {
                registry.register(new HardcoreHeartsModule(respawnModule));
            }
        }

        registry.register(new PercentHealthObjectiveModule());
        setup(new PlayerListHealthCommand(
                forCommand("showhealth"),
                Bukkit.getScoreboardManager().getMainScoreboard(),
                DisplaySlot.PLAYER_LIST,
                "UHCHealth",
                "Health"
        ), "showhealth");

        PotionFuelsListener fuelsListener = new PotionFuelsListener();
        registry.registerEvents(fuelsListener);
        registry.register(new Tier2PotionsModule(fuelsListener));
        registry.register(new SplashPotionsModule(fuelsListener));

        PlayerHeadProvider headProvider = new PlayerHeadProvider();
        GoldenHeadsModule gheadModule = new GoldenHeadsModule(headProvider);
        boolean gheadsLoaded = registry.register(gheadModule);
        setup(gheadsLoaded ? new GoldenHeadsHealthCommand(forCommand("ghead"), gheadModule) : dummyCommands.forModule(gheadModule), "ghead");
        registry.register(new HeadDropsModule(headProvider));
        registry.register(new DeathStandsModule());

        TeamModule teamModule = new TeamModule();
        if (registry.register(teamModule)) {
            setup(new ListTeamsCommand(forCommand("teams"), teamModule), "teams");
            setup(new NoTeamCommand(forCommand("noteam"), teamModule), "noteam");
            setup(new TeamPMCommand(forCommand("pmt"), teamModule), "pmt");
            setup(new RandomTeamsCommand(forCommand("randomteams"), teamModule), "randomteams");
            setup(new ClearTeamsCommand(forCommand("clearteams"), teamModule), "clearteams");
            setup(new TeamCoordinatesCommand(forCommand("tc"), teamModule), "tc");

            SubcommandCommand team = new SubcommandCommand();
            team.registerSubcommand("teamup", new TeamupCommand(forCommand("team.teamup"), teamModule));
            team.registerSubcommand("add", new TeamAddCommand(forCommand("team.add"), teamModule));
            team.registerSubcommand("remove", new TeamRemoveCommand(forCommand("team.remove"), teamModule));
            setup(team , "team");

            MessageTemplates requestMessages = forCommand("teamrequest");
            RequestManager requestManager = new RequestManager(this, requestMessages, teamModule, 20 * 120);

            SubcommandCommand teamrequest = new SubcommandCommand();
            teamrequest.registerSubcommand("accept", new RequestResponseCommand(requestMessages, requestManager, RequestManager.AcceptState.ACCEPT));
            teamrequest.registerSubcommand("deny", new RequestResponseCommand(requestMessages, requestManager, RequestManager.AcceptState.DENY));
            teamrequest.registerSubcommand("request", new TeamRequestCommand(requestMessages, requestManager));
            teamrequest.registerSubcommand("list", new RequestListCommand(requestMessages, requestManager));
            setup(teamrequest, "teamrequest");
        } else {
            setup(dummyCommands.forModule(teamModule), "teams", "team", "noteam", "pmt", "randomteams", "clearteams", "tc", "teamrequest");
        }

        setup(new WorldBorderCommand(forCommand("border")), "border");
        setup(new ModuleCommands(forCommand("uhc"), registry), "uhc");

        long cacheTicks = 30 * 20;
        setup(new PlayerAffectingCommand(forCommand("heal"), new PlayerHealthResetter(this, cacheTicks)), "heal");
        setup(new PlayerAffectingCommand(forCommand("feed"), new PlayerFoodResetter(this, cacheTicks)), "feed");
        setup(new PlayerAffectingCommand(forCommand("clearxp"), new PlayerXPResetter(this, cacheTicks)), "clearxp");
        setup(new PlayerAffectingCommand(forCommand("ci"), new PlayerInventoryResetter(this, cacheTicks)), "ci");
        setup(new PlayerAffectingCommand(forCommand("cleareffects"), new PlayerPotionsResetter(this, cacheTicks)), "cleareffects");
        setup(new PlayerAffectingCommand(forCommand("reset"), new FullPlayerResetter(this, cacheTicks)), "reset");

        setup(new TeleportCommand(forCommand("tpp")), "tpp");
        setup(new HealthCommand(forCommand("h")), "h");

        SubcommandCommand wlist = new SubcommandCommand();
        MessageTemplates forWlist = forCommand("wlist");
        wlist.registerSubcommand("clear", new WhitelistClearCommand(forWlist));
        wlist.registerSubcommand(SubcommandCommand.NO_ARG_SPECIAL, new WhitelistOnlineCommand(forWlist));
        setup(wlist, "wlist");

        // save config just to make sure at the end
        saveConfig();
    }

    protected void setup(CommandExecutor executor, String... commands) {
        for (String command : commands) {
            getCommand(command).setExecutor(executor);
        }
    }

    private MessageTemplates forCommand(String command) {
        return new SubsectionMessageTemplates(baseCommandMessages, command);
    }

    @Override
    public void saveConfig() {
        configSaver.trigger();
    }

    public void saveConfigNow() {
        super.saveConfig();
        getLogger().info("Saved configuration changes");
    }

    public ModuleRegistry getRegistry() {
        return registry;
    }

    protected Config setupMessagesConfig() throws IOException {
        // copy reference across
        saveResource("messages.reference.conf", true);

        // parse fallback config
        File reference = new File(getDataFolder(), "messages.reference.conf");
        Config fallback = ConfigFactory.parseFile(reference);

        // parse user provided config
        File regular = new File(getDataFolder(), "messages.conf");

        Config user;
        if (regular.exists()) {
            user = ConfigFactory.parseFile(regular);
        } else {
            user = ConfigFactory.empty();
        }

        return user.withFallback(fallback).resolve(ConfigResolveOptions.noSystem());
    }
}
