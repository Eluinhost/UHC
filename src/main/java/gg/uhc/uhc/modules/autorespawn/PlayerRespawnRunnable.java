package gg.uhc.uhc.modules.autorespawn;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;

public class PlayerRespawnRunnable extends BukkitRunnable {

    protected final WeakReference<Player> player;

    /**
     * Respawns the player when ran. If the player object has been GCd or the player no longer has 0 health then nothing
     * will happen.
     *
     * @param player the player to respawn
     */
    public PlayerRespawnRunnable(Player player) {
        this.player = new WeakReference<>(player);
    }

    @Override
    public void run() {
        Player p = player.get();

        if (p == null || p.getHealth() != 0) return;

        p.spigot().respawn();
    }
}
