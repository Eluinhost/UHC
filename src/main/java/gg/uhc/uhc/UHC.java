package gg.uhc.uhc;

import gg.uhc.flagcommands.commands.SubcommandCommand;
import gg.uhc.uhc.inventory.ShowIconsCommand;
import gg.uhc.uhc.modules.ModuleNotLoadedDummyCommand;
import gg.uhc.uhc.modules.ModuleRegistry;
import gg.uhc.uhc.modules.autorespawn.AutoRespawnModule;
import gg.uhc.uhc.modules.border.WorldBorderCommand;
import gg.uhc.uhc.modules.commands.ModuleCommands;
import gg.uhc.uhc.modules.difficulty.DifficultyModule;
import gg.uhc.uhc.modules.enderpearls.EnderpearlsModule;
import gg.uhc.uhc.modules.food.ExtendedSaturationModule;
import gg.uhc.uhc.modules.food.FeedCommand;
import gg.uhc.uhc.modules.heads.GoldenHeadsHealthCommand;
import gg.uhc.uhc.modules.heads.GoldenHeadsModule;
import gg.uhc.uhc.modules.heads.HeadDropsModule;
import gg.uhc.uhc.modules.heads.PlayerHeadProvider;
import gg.uhc.uhc.modules.health.*;
import gg.uhc.uhc.modules.inventory.ClearInventoryCommand;
import gg.uhc.uhc.modules.inventory.ClearXPCommand;
import gg.uhc.uhc.modules.inventory.ResetPlayerCommand;
import gg.uhc.uhc.modules.portals.NetherModule;
import gg.uhc.uhc.modules.potions.*;
import gg.uhc.uhc.modules.pvp.GlobalPVPModule;
import gg.uhc.uhc.modules.recipes.GlisteringMelonRecipeModule;
import gg.uhc.uhc.modules.recipes.GoldenCarrotRecipeModule;
import gg.uhc.uhc.modules.recipes.NotchApplesModule;
import gg.uhc.uhc.modules.team.*;
import gg.uhc.uhc.modules.team.requests.RequestListCommand;
import gg.uhc.uhc.modules.team.requests.RequestManager;
import gg.uhc.uhc.modules.team.requests.RequestResponseCommand;
import gg.uhc.uhc.modules.team.requests.TeamRequestCommand;
import gg.uhc.uhc.modules.teleport.TeleportCommand;
import gg.uhc.uhc.modules.timer.TimerCommand;
import gg.uhc.uhc.modules.timer.TimerModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

public class UHC extends JavaPlugin {

    protected ModuleRegistry registry;
    protected DebouncedRunnable configSaver;

    @Override
    public void onEnable() {
        // setup to save the config with a debounce of 2 seconds
        configSaver = new DebouncedRunnable(this, new Runnable() {
            @Override
            public void run() {
                saveConfigNow();
            }
        }, 40);

        registry = new ModuleRegistry(this, getConfig());

        registry.register(new DifficultyModule(), "HardDifficulty");
        registry.register(new HealthRegenerationModule(), "HealthRegen");
        registry.register(new GhastTearDropsModule(), "GhastTears");
        registry.register(new GoldenCarrotRecipeModule(), "GoldenCarrotRecipe");
        registry.register(new GlisteringMelonRecipeModule(), "GlisteringMelonRecipe");
        registry.register(new NotchApplesModule(), "NotchApples");
        registry.register(new AbsorptionModule(), "Absoption");
        registry.register(new ExtendedSaturationModule(), "ExtendedSaturation");
        registry.register(new GlobalPVPModule(), "PVP");
        registry.register(new EnderpearlsModule(), "EnderpearlDamage");
        registry.register(new WitchesModule(), "WitchSpawns");
        registry.register(new NetherModule(), "Nether");

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

        PercentHealthObjectiveModule percentHealth = new PercentHealthObjectiveModule();
        boolean percentHealthLoaded = registry.register(percentHealth, "PercentHealth");
        getCommand("showhealth").setExecutor(new PlayerListHealthCommand(
                percentHealthLoaded ? percentHealth : null,
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
        getCommand("ghead").setExecutor(gheadsLoaded ? new GoldenHeadsHealthCommand(gheadModule) : new ModuleNotLoadedDummyCommand("GoldenHeads"));
        registry.register(new HeadDropsModule(headProvider), "HeadDrops");

        TeamModule teamModule = new TeamModule();
        if (registry.register(teamModule, "TeamManager")) {
            getCommand("teams").setExecutor(new ListTeamsCommand(teamModule));
            getCommand("team").setExecutor(new TeamCommands(teamModule));
            getCommand("noteam").setExecutor(new NoTeamCommand(teamModule));
            getCommand("pmt").setExecutor(new TeamPMCommand(teamModule));
            getCommand("randomteams").setExecutor(new RandomTeamsCommand(teamModule));
            getCommand("clearteams").setExecutor(new ClearTeamsCommand(teamModule));

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
            getCommand("teamrequest").setExecutor(teamsNotLoaded);
        }

        getCommand("border").setExecutor(new WorldBorderCommand());
        getCommand("addons").setExecutor(new ShowIconsCommand(registry.getInventory()));
        getCommand("uhc").setExecutor(new ModuleCommands(registry));

        PlayerResetter resetter = new PlayerResetter();
        getCommand("heal").setExecutor(new HealCommand(resetter));
        getCommand("feed").setExecutor(new FeedCommand(resetter));
        getCommand("clearxp").setExecutor(new ClearXPCommand(resetter));
        getCommand("ci").setExecutor(new ClearInventoryCommand(resetter));
        getCommand("reset").setExecutor(new ResetPlayerCommand(resetter));
        getCommand("cleareffects").setExecutor(new ClearPotionsCommand(resetter));

        getCommand("tpp").setExecutor(new TeleportCommand());

        // save config just to make sure at the end
        saveConfig();
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
}
