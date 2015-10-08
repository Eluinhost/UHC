package gg.uhc.uhc.modules.portals;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.team.FunctionalUtil;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.Set;

public class NetherModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Nether";

    public NetherModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.NETHER_STALK);
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Travelling to the nether is enabled");
        } else {
            icon.setLore("Travelling to the nether is disabled");
        }
    }

    @Override
    public void onDisable() {
        Set<OfflinePlayer> players = Sets.newHashSet();
        Set<String> worlds = Sets.newHashSet();

        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER) {
                worlds.add(world.getName());
                players.addAll(world.getPlayers());
            }
        }

        if (players.size() == 0) return;

        Joiner joiner = Joiner.on(", ");
        String playerNames = joiner.join(Iterables.transform(players, FunctionalUtil.PLAYER_NAME_FETCHER));
        String worldNames = joiner.join(worlds);

        Bukkit.broadcast(ChatColor.DARK_GRAY + "The player/s [" + playerNames + "] are within the nether world/s: [" + worldNames + "].", "uhc.broadcast.netherdisable");
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityPortalEvent event) {
        if (isEnabled()) return;

        if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER)
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerPortalEvent event) {
        if (isEnabled()) return;

        if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER)
            event.setCancelled(true);
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }
}
