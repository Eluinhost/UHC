package gg.uhc.uhc;

import com.google.common.collect.ImmutableList;
import gg.uhc.uhc.inventory.IconInventory;
import gg.uhc.uhc.command.ShowIconsCommand;
import gg.uhc.uhc.modules.ConfigurableModule;
import gg.uhc.uhc.modules.difficulty.DifficultyModule;
import gg.uhc.uhc.modules.heads.GoldenHeadsModule;
import gg.uhc.uhc.modules.heads.HeadDropsModule;
import gg.uhc.uhc.modules.heads.PlayerHeadProvider;
import gg.uhc.uhc.modules.health.GhastTearDropsModule;
import gg.uhc.uhc.modules.health.HealthRegenerationModule;
import gg.uhc.uhc.modules.health.PlayerListHealthCommand;
import gg.uhc.uhc.modules.potions.AbsorptionModule;
import gg.uhc.uhc.modules.recipes.GlisteringMelonRecipeModule;
import gg.uhc.uhc.modules.recipes.GoldenCarrotRecipeModule;
import gg.uhc.uhc.modules.recipes.NotchApplesModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;

public class UHC extends JavaPlugin {

    protected IconInventory inventory;

    @Override
    public void onEnable() {
        inventory = new IconInventory(ChatColor.DARK_PURPLE + "UHC Control Panel");
        registerEvents(inventory);

        PlayerHeadProvider headProvider = new PlayerHeadProvider();

        // TODO config
        List<ConfigurableModule> modules = ImmutableList.<ConfigurableModule>builder()
                .add(new HealthRegenerationModule(inventory.createNewIcon(1), false))
                .add(new GhastTearDropsModule(inventory.createNewIcon(1), false))
                .add(new GoldenCarrotRecipeModule(inventory.createNewIcon(3), true))
                .add(new GlisteringMelonRecipeModule(inventory.createNewIcon(3), true))
                .add(new NotchApplesModule(inventory.createNewIcon(3), false))
                .add(new AbsorptionModule(this, inventory.createNewIcon(2), false))
                .add(new GoldenHeadsModule(headProvider, inventory.createNewIcon(0), true, 7))
                .add(new HeadDropsModule(inventory.createNewIcon(0), true, 1F, headProvider))
                .add(new DifficultyModule(inventory.createNewIcon(1), true))
                .build();

        // TODO global pvp
        // TODO enderpearls
        // TODO death message removal
        // TODO death bans?
        // TODO death items?
        // TODO tier 2
        // TODO splash potions

        for (ConfigurableModule module : modules) {
            if (module instanceof Listener) {
                registerEvents((Listener) module);
            }
        }

        // TODO heal
        // TODO feed
        // TODO clear inventory
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
    }

    protected void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public IconInventory getIconInventory() {
        return inventory;
    }
}
