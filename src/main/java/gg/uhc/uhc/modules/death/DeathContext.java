/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.death.DeathContext
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

package gg.uhc.uhc.modules.death;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import java.text.NumberFormat;

public class DeathContext {

    protected static final NumberFormat formatter = NumberFormat.getInstance();

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    protected final String original;
    protected final PlayerWrapper player;
    protected final PlayerWrapper killer;

    public DeathContext(PlayerDeathEvent event) {
        this.original = event.getDeathMessage();
        this.player = new PlayerWrapper(event.getEntity());

        Player killer = event.getEntity().getKiller();
        System.out.println(killer);
        this.killer = killer == null ? null : new PlayerWrapper(killer);
    }

    public String cause() {
        return player.bukkitPlayer.getLastDamageCause().getCause().name();
    }

    public boolean hasKiller() {
        return killer != null;
    }

    public static class PlayerWrapper {

        protected Player bukkitPlayer;

        public PlayerWrapper(Player player) {
            this.bukkitPlayer = player;
        }

        public String name() {
            return bukkitPlayer.getName();
        }

        public String displayName() {
            return bukkitPlayer.getDisplayName();
        }

        public Vector rawCoords() {
            return bukkitPlayer.getLocation().toVector();
        }

        public String blockCoords() {
            Vector coords = rawCoords();
            return coords.getBlockX() + "," + coords.getBlockY() + "," + coords.getBlockZ();
        }

        public String world() {
            return bukkitPlayer.getWorld().getName();
        }

        public String environment() {
            return bukkitPlayer.getWorld().getEnvironment().name();
        }

        public String health() {
            return formatter.format(bukkitPlayer.getHealth());
        }

        public String maxHealth() {
            return formatter.format(bukkitPlayer.getMaxHealth());
        }

        public String percentHealth() {
            return formatter.format(bukkitPlayer.getHealth() / bukkitPlayer.getMaxHealth() * 100D);
        }
    }
}
