package gg.uhc.uhc.modules.food;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SaturationMultiplierRunnable extends BukkitRunnable {

    protected final UUID uuid;
    protected final float pre;
    protected final double multiplier;

    public SaturationMultiplierRunnable(UUID uuid, float pre, double multiplier) {
        this.uuid = uuid;
        this.pre = pre;
        this.multiplier = multiplier;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        float change = player.getSaturation() - pre;

        player.setSaturation(pre + (change * ((float)multiplier)));
    }
}
