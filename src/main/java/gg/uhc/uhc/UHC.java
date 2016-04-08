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

import com.google.common.base.Optional;
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
import gg.uhc.uhc.modules.difficulty.PermadayCommand;
import gg.uhc.uhc.modules.enderpearls.EnderpearlsModule;
import gg.uhc.uhc.modules.food.ExtendedSaturationModule;
import gg.uhc.uhc.modules.heads.GoldenHeadsHealthCommand;
import gg.uhc.uhc.modules.heads.GoldenHeadsModule;
import gg.uhc.uhc.modules.heads.HeadDropsModule;
import gg.uhc.uhc.modules.heads.PlayerHeadProvider;
import gg.uhc.uhc.modules.health.*;
import gg.uhc.uhc.modules.horses.HorseArmourModule;
import gg.uhc.uhc.modules.horses.HorseHealingModule;
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.io.IOException;

public class UHC extends JavaPlugin {

    protected ModuleRegistry registry;
    protected DebouncedRunnable configSaver;
    protected DummyCommandFactory dummyCommands;
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
        dummyCommands = new DummyCommandFactory(baseCommandMessages);

        registry = new ModuleRegistry(this, baseMessages, configuration);

        setupBasicModules();
        setupProtocolLibModules();

        setupCommand(new PlayerListHealthCommand(
                commandMessages("showhealth"),
                Bukkit.getScoreboardManager().getMainScoreboard(),
                DisplaySlot.PLAYER_LIST,
                "UHCHealth",
                "Health"
        ), "showhealth");

        PotionFuelsListener fuelsListener = new PotionFuelsListener();
        registry.registerEvents(fuelsListener);
        registry.register(new Tier2PotionsModule(fuelsListener));
        registry.register(new SplashPotionsModule(fuelsListener));

        TimerModule timer = new TimerModule();
        setupCommand(registry.register(timer) ? new TimerCommand(commandMessages("timer"), timer) : dummyCommands.forModule(timer), "timer");

        PlayerHeadProvider headProvider = new PlayerHeadProvider();
        GoldenHeadsModule gheadModule = new GoldenHeadsModule();
        boolean gheadsLoaded = registry.register(gheadModule);
        setupCommand(gheadsLoaded ? new GoldenHeadsHealthCommand(commandMessages("ghead"), gheadModule) : dummyCommands.forModule(gheadModule), "ghead");
        registry.register(new HeadDropsModule(headProvider));
        registry.register(new DeathStandsModule());

        setupTeamCommands();

        setupCommand(new WorldBorderCommand(commandMessages("border")), "border");
        setupCommand(new ModuleCommands(commandMessages("uhc"), registry), "uhc");
        setupCommand(new PermadayCommand(commandMessages("permaday")), "permaday");

        long cacheTicks = 30 * 20;
        setupCommand(new PlayerAffectingCommand(commandMessages("heal"), new PlayerHealthResetter(this, cacheTicks)), "heal");
        setupCommand(new PlayerAffectingCommand(commandMessages("feed"), new PlayerFoodResetter(this, cacheTicks)), "feed");
        setupCommand(new PlayerAffectingCommand(commandMessages("clearxp"), new PlayerXPResetter(this, cacheTicks)), "clearxp");
        setupCommand(new PlayerAffectingCommand(commandMessages("ci"), new PlayerInventoryResetter(this, cacheTicks)), "ci");
        setupCommand(new PlayerAffectingCommand(commandMessages("cleareffects"), new PlayerPotionsResetter(this, cacheTicks)), "cleareffects");
        setupCommand(new PlayerAffectingCommand(commandMessages("reset"), new FullPlayerResetter(this, cacheTicks)), "reset");

        setupCommand(new TeleportCommand(commandMessages("tpp")), "tpp");
        setupCommand(new HealthCommand(commandMessages("h"), 200D), "h");

        SubcommandCommand wlist = new SubcommandCommand();
        MessageTemplates forWlist = commandMessages("wlist");
        wlist.registerSubcommand("clear", new WhitelistClearCommand(forWlist));
        wlist.registerSubcommand(SubcommandCommand.NO_ARG_SPECIAL, new WhitelistOnlineCommand(forWlist));
        setupCommand(wlist, "wlist");

        // save config just to make sure at the end
        saveConfig();
    }

    @Override
    public void onDisable() {
        Bukkit.getPluginManager().callEvent(new PluginDisableEvent());
    }

    protected void setupTeamCommands() {
        Optional<TeamModule> teamModuleOptional = registry.get(TeamModule.class);

        if (!teamModuleOptional.isPresent()) {
            getLogger().info("Skipping registering team commands as the team module is not loaded");
            setupCommand(dummyCommands.forModule("TeamModule"), "teams", "team", "noteam", "pmt", "randomteams", "clearteams", "tc", "teamrequest");
            return;
        }

        TeamModule teamModule = teamModuleOptional.get();

        setupCommand(new ListTeamsCommand(commandMessages("teams"), teamModule), "teams");
        setupCommand(new NoTeamCommand(commandMessages("noteam"), teamModule), "noteam");
        setupCommand(new TeamPMCommand(commandMessages("pmt"), teamModule), "pmt");
        setupCommand(new RandomTeamsCommand(commandMessages("randomteams"), teamModule), "randomteams");
        setupCommand(new ClearTeamsCommand(commandMessages("clearteams"), teamModule), "clearteams");
        setupCommand(new TeamCoordinatesCommand(commandMessages("tc"), teamModule), "tc");

        SubcommandCommand team = new SubcommandCommand();
        team.registerSubcommand("teamup", new TeamupCommand(commandMessages("team.teamup"), teamModule));
        team.registerSubcommand("add", new TeamAddCommand(commandMessages("team.add"), teamModule));
        team.registerSubcommand("remove", new TeamRemoveCommand(commandMessages("team.remove"), teamModule));
        setupCommand(team , "team");

        ConfigurationSection teamModuleConfig = teamModule.getConfig();
        if (!teamModuleConfig.isSet(RequestManager.AUTO_WHITELIST_KEY)) {
            teamModuleConfig.set(RequestManager.AUTO_WHITELIST_KEY, true);
        }
        boolean autoWhitelistAcceptedTeams = teamModuleConfig.getBoolean(RequestManager.AUTO_WHITELIST_KEY);

        MessageTemplates requestMessages = commandMessages("team.teamrequest");
        RequestManager requestManager = new RequestManager(this, requestMessages, teamModule, 20 * 120, autoWhitelistAcceptedTeams);

        SubcommandCommand teamrequest = new SubcommandCommand();
        teamrequest.registerSubcommand("accept", new RequestResponseCommand(requestMessages, requestManager, RequestManager.AcceptState.ACCEPT));
        teamrequest.registerSubcommand("deny", new RequestResponseCommand(requestMessages, requestManager, RequestManager.AcceptState.DENY));
        teamrequest.registerSubcommand("request", new TeamRequestCommand(requestMessages, requestManager));
        teamrequest.registerSubcommand("list", new RequestListCommand(requestMessages, requestManager));
        setupCommand(teamrequest, "teamrequest");
    }

    protected void setupProtocolLibModules() {
        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().info("Skipping hardcore hearts module because protocollib is not installed");
            return;
        }

        Optional<AutoRespawnModule> respawn = registry.get(AutoRespawnModule.class);

        if (!respawn.isPresent()) {
            getLogger().info("Skipping hardcore hearts module because auto respawn is not enabled");
            return;
        }

        registry.register(new HardcoreHeartsModule(respawn.get()));
    }

    protected void setupBasicModules() {
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
        registry.register(new HorseHealingModule());
        registry.register(new HorseArmourModule());
        registry.register(new DeathLightningModule());
        registry.register(new ModifiedDeathMessagesModule());
        registry.register(new DeathItemsModule());
        registry.register(new ChatHealthPrependModule());
        registry.register(new NerfQuartzXPModule());
        registry.register(new AutoRespawnModule());
        registry.register(new PercentHealthObjectiveModule());
        registry.register(new TeamModule());
    }

    protected void setupCommand(CommandExecutor executor, String... commands) {
        for (String command : commands) {
            getCommand(command).setExecutor(executor);
        }
    }

    private MessageTemplates commandMessages(String command) {
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
