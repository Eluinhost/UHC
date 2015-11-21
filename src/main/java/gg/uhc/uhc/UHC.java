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
import gg.uhc.uhc.modules.ModuleNotLoadedDummyCommand;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.autorespawn.AutoRespawnModule;
import gg.uhc.uhc.modules.border.WorldBorderCommand;
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

        registry = new ModuleRegistry(this, baseMessages, configuration);

        registry.register(new DifficultyModule(), "HardDifficulty");
        registry.register(new HealthRegenerationModule(), "HealthRegen");
        registry.register(new GhastTearDropsModule(), "GhastTears");
        registry.register(new GoldenCarrotRecipeModule(), "GoldenCarrotRecipe");
        registry.register(new GlisteringMelonRecipeModule(), "GlisteringMelonRecipe");
        registry.register(new NotchApplesModule(), "NotchApples");
        registry.register(new AbsorptionModule(), "Absorption");
        registry.register(new ExtendedSaturationModule(), "ExtendedSaturation");
        registry.register(new GlobalPVPModule(), "PVP");
        registry.register(new EnderpearlsModule(), "EnderpearlDamage");
        registry.register(new WitchesModule(), "WitchSpawns");
        registry.register(new NetherModule(), "Nether");
        registry.register(new DeathBansModule(), "DeathBans");
        registry.register(new HorsesModule(), "Horses");
        registry.register(new HorseArmourModule(), "HorseArmour");
        registry.register(new DeathLightningModule(), "DeathLightning");
        registry.register(new ModifiedDeathMessagesModule(), "DeathMessages");
        registry.register(new DeathItemsModule(), "DeathItems");
        registry.register(new ChatHealthPrependModule(), "ChatHealth");
        registry.register(new NerfQuartzXPModule(), "NerfQuartzXP");

        AutoRespawnModule respawnModule = new AutoRespawnModule();
        boolean respawnModuleLoaded = registry.register(respawnModule, "AutoRespawn");

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            TimerModule timer = new TimerModule();

            boolean timerLoaded = registry.register(timer, "Timer");
            getCommand("timer").setExecutor(timerLoaded ? new TimerCommand(timer) : new ModuleNotLoadedDummyCommand("Timer"));

            if (respawnModuleLoaded) {
                registry.register(new HardcoreHeartsModule(respawnModule), "HardcoreHearts");
            }
        }

        registry.register(new PercentHealthObjectiveModule(), "PercentHealth");
        getCommand("showhealth").setExecutor(new PlayerListHealthCommand(
                forCommand("showhealth"),
                Bukkit.getScoreboardManager().getMainScoreboard(),
                DisplaySlot.PLAYER_LIST,
                "UHCHealth",
                "Health"
        ));

        PotionFuelsListener fuelsListener = new PotionFuelsListener();
        registry.registerEvents(fuelsListener);
        registry.register(new Tier2PotionsModule(fuelsListener), "Tier2Potions");
        registry.register(new SplashPotionsModule(fuelsListener), "SplashPotions");

        PlayerHeadProvider headProvider = new PlayerHeadProvider();
        GoldenHeadsModule gheadModule = new GoldenHeadsModule(headProvider);
        boolean gheadsLoaded = registry.register(gheadModule, "GoldenHeads");
        getCommand("ghead").setExecutor(gheadsLoaded ? new GoldenHeadsHealthCommand(forCommand("ghead"), gheadModule) : new ModuleNotLoadedDummyCommand("GoldenHeads"));
        registry.register(new HeadDropsModule(headProvider), "HeadDrops");
        registry.register(new DeathStandsModule(), "DeathStands");

        TeamModule teamModule = new TeamModule();
        if (registry.register(teamModule, "TeamManager")) {
            getCommand("teams").setExecutor(new ListTeamsCommand(teamModule));
            getCommand("team").setExecutor(new TeamCommands(teamModule));
            getCommand("noteam").setExecutor(new NoTeamCommand(teamModule));
            getCommand("pmt").setExecutor(new TeamPMCommand(teamModule));
            getCommand("randomteams").setExecutor(new RandomTeamsCommand(teamModule));
            getCommand("clearteams").setExecutor(new ClearTeamsCommand(teamModule));
            getCommand("tc").setExecutor(new TeamCoordinatesCommand(teamModule));

            RequestManager requestManager = new RequestManager(this, teamModule, 20 * 120);
            SubcommandCommand teamrequest = new SubcommandCommand();
            teamrequest.registerSubcommand("accept", new RequestResponseCommand(requestManager, RequestManager.AcceptState.ACCEPT));
            teamrequest.registerSubcommand("deny", new RequestResponseCommand(requestManager, RequestManager.AcceptState.DENY));
            teamrequest.registerSubcommand("request", new TeamRequestCommand(requestManager));
            teamrequest.registerSubcommand("list", new RequestListCommand(requestManager));
            getCommand("teamrequest").setExecutor(teamrequest);
        } else {
            CommandExecutor teamsNotLoaded = new ModuleNotLoadedDummyCommand("TeamManager");
            getCommand("teams").setExecutor(teamsNotLoaded);
            getCommand("team").setExecutor(teamsNotLoaded);
            getCommand("noteam").setExecutor(teamsNotLoaded);
            getCommand("pmt").setExecutor(teamsNotLoaded);
            getCommand("randomteams").setExecutor(teamsNotLoaded);
            getCommand("clearteams").setExecutor(teamsNotLoaded);
            getCommand("tc").setExecutor(teamsNotLoaded);
            getCommand("teamrequest").setExecutor(teamsNotLoaded);
        }

        getCommand("border").setExecutor(new WorldBorderCommand(forCommand("border")));
        getCommand("uhc").setExecutor(new ModuleCommands(forCommand("uhc"), registry));

        long cacheTicks = 30 * 20;
        getCommand("heal").setExecutor(new PlayerAffectingCommand(forCommand("heal"), new PlayerHealthResetter(this, cacheTicks)));
        getCommand("feed").setExecutor(new PlayerAffectingCommand(forCommand("feed"), new PlayerFoodResetter(this, cacheTicks)));
        getCommand("clearxp").setExecutor(new PlayerAffectingCommand(forCommand("clearxp"), new PlayerXPResetter(this, cacheTicks)));
        getCommand("ci").setExecutor(new PlayerAffectingCommand(forCommand("ci"), new PlayerInventoryResetter(this, cacheTicks)));
        getCommand("cleareffects").setExecutor(new PlayerAffectingCommand(forCommand("cleareffects"), new PlayerPotionsResetter(this, cacheTicks)));
        getCommand("reset").setExecutor(new PlayerAffectingCommand(forCommand("reset"), new FullPlayerResetter(this, cacheTicks)));

        getCommand("tpp").setExecutor(new TeleportCommand());
        getCommand("h").setExecutor(new HealthCommand(forCommand("h")));

        SubcommandCommand wlist = new SubcommandCommand();
        MessageTemplates forWlist = forCommand("wlist");
        wlist.registerSubcommand("clear", new WhitelistClearCommand(forWlist));
        wlist.registerSubcommand(SubcommandCommand.NO_ARG_SPECIAL, new WhitelistOnlineCommand(forWlist));
        getCommand("wlist").setExecutor(wlist);

        // save config just to make sure at the end
        saveConfig();
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
