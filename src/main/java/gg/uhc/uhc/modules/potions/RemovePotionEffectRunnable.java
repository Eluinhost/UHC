package gg.uhc.uhc.modules.potions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class RemovePotionEffectRunnable extends BukkitRunnable {

    protected final UUID playerID;
    protected final PotionEffectType[] effects;

    public RemovePotionEffectRunnable(UUID playerID, PotionEffectType... potions) {
        this.playerID = playerID;
        this.effects = potions;
    }

    @Override
    public void run() {
        Player p = Bukkit.getPlayer(playerID);

        if (p == null) return;

        for (PotionEffectType type : effects) {
            p.removePotionEffect(type);
        }
    }
}
