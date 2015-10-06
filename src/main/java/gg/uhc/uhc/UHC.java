package gg.uhc.uhc;

import com.google.common.collect.Maps;
import gg.uhc.uhc.command.ShowIconsCommand;
import gg.uhc.uhc.inventory.IconInventory;
import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.border.WorldBorderCommand;
import gg.uhc.uhc.modules.difficulty.DifficultyModule;
import gg.uhc.uhc.modules.enderpearls.EnderpearlsModule;
import gg.uhc.uhc.modules.food.FeedCommand;
import gg.uhc.uhc.modules.heads.GoldenHeadsHealthCommand;
import gg.uhc.uhc.modules.heads.GoldenHeadsModule;
import gg.uhc.uhc.modules.heads.HeadDropsModule;
import gg.uhc.uhc.modules.heads.PlayerHeadProvider;
import gg.uhc.uhc.modules.health.GhastTearDropsModule;
import gg.uhc.uhc.modules.health.HealCommand;
import gg.uhc.uhc.modules.health.HealthRegenerationModule;
import gg.uhc.uhc.modules.health.PlayerListHealthCommand;
import gg.uhc.uhc.modules.inventory.ClearInventoryCommand;
import gg.uhc.uhc.modules.inventory.ClearXPCommand;
import gg.uhc.uhc.modules.potions.*;
import gg.uhc.uhc.modules.inventory.ResetPlayerCommand;
import gg.uhc.uhc.modules.pvp.GlobalPVPModule;
import gg.uhc.uhc.modules.recipes.GlisteringMelonRecipeModule;
import gg.uhc.uhc.modules.recipes.GoldenCarrotRecipeModule;
import gg.uhc.uhc.modules.recipes.NotchApplesModule;
import gg.uhc.uhc.modules.food.ExtendedSaturationModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Map;

public class UHC extends JavaPlugin {

    protected Map<String, Module> modules;
    protected IconInventory inventory;
    protected FileConfiguration configuration;
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

        configuration = getConfig();

        modules = Maps.newHashMap();

        inventory = new IconInventory(ChatColor.DARK_PURPLE + "UHC Control Panel");
        registerEvents(inventory);

        PlayerHeadProvider headProvider = new PlayerHeadProvider();

        // TODO configuration to stop modules loading at all
        registerModule("hard difficulty", new DifficultyModule());
        registerModule("head drops", new HeadDropsModule(headProvider));
        registerModule("health regeneration", new HealthRegenerationModule());
        registerModule("ghast tears", new GhastTearDropsModule());
        registerModule("golden carrot recipe", new GoldenCarrotRecipeModule());
        registerModule("glistering melon recipe", new GlisteringMelonRecipeModule());
        registerModule("notch apples", new NotchApplesModule());
        registerModule("absoption", new AbsorptionModule());
        registerModule("extended saturation", new ExtendedSaturationModule());
        registerModule("pvp", new GlobalPVPModule());
        registerModule("enderpearl damage", new EnderpearlsModule());

        PotionFuelsListener fuelsListener = new PotionFuelsListener();
        registerEvents(fuelsListener);

        registerModule("tier 2 potions", new Tier2PotionsModule(fuelsListener));
        registerModule("splash potions", new SplashPotionsModule(fuelsListener));

        GoldenHeadsModule gheadModule = new GoldenHeadsModule(headProvider);
        registerModule("golden heads", gheadModule);
        getCommand("ghead").setExecutor(new GoldenHeadsHealthCommand(gheadModule));

        // TODO death message removal
        // TODO death bans?
        // TODO death items?
        // TODO tier 2
        // TODO splash potions
        // TODO tpp?
        // TODO team commands

        getCommand("addons").setExecutor(new ShowIconsCommand(inventory));

        // TODO config
        getCommand("showhealth").setExecutor(new PlayerListHealthCommand(
                Bukkit.getScoreboardManager().getMainScoreboard(),
                DisplaySlot.PLAYER_LIST,
                "UHCHealth",
                "Health"
        ));

        PlayerResetter resetter = new PlayerResetter();

        getCommand("heal").setExecutor(new HealCommand(resetter));
        getCommand("feed").setExecutor(new FeedCommand(resetter));
        getCommand("clearxp").setExecutor(new ClearXPCommand(resetter));
        getCommand("ci").setExecutor(new ClearInventoryCommand(resetter));
        getCommand("reset").setExecutor(new ResetPlayerCommand(resetter));
        getCommand("cleareffects").setExecutor(new ClearPotionsCommand(resetter));

        getCommand("border").setExecutor(new WorldBorderCommand());

        saveConfig();
    }

    protected void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerModule(String id, Module module) {
        if (modules.containsKey(id)) {
            throw new IllegalArgumentException("Module " + module + " is already registered");
        }

        module.setPlugin(this);

        String sectionId = "modules." + id;

        if (!configuration.contains(sectionId)) {
            configuration.createSection(sectionId);
        }

        try {
            module.initialize(configuration.getConfigurationSection(sectionId));
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
            return;
        }

        modules.put(id, module);

        if (module instanceof Listener) {
            registerEvents((Listener) module);
        }

        inventory.registerNewIcon(module.getIconStack());
    }

    @Override
    public void saveConfig() {
        configSaver.trigger();
    }

    public void saveConfigNow() {
        super.saveConfig();
        getLogger().info("Saved configuration changes");
    }
}
