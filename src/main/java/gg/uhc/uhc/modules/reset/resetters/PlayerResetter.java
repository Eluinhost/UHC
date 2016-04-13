/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.reset.resetters.PlayerResetter
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

package gg.uhc.uhc.modules.reset.resetters;

import gg.uhc.uhc.modules.reset.actions.Action;

import com.google.common.collect.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class PlayerResetter {

    protected final Plugin plugin;

    private final Multimap<String, Action> cache = HashMultimap.create();
    private final Map<String, BukkitRunnable> removeTasks = Maps.newHashMap();
    private final long cacheTicks;

    public PlayerResetter(Plugin plugin, long cacheTicks) {
        this.plugin = plugin;
        this.cacheTicks = cacheTicks;
    }

    public long getCacheTicks() {
        return cacheTicks;
    }

    protected abstract Action getActionForPlayer(Player player);

    /**
     * Creates actions to reset each of the players.
     *
     * @param players the players to create for
     * @param key the cache key to store the actions under
     * @return list of actions to apply for the given players
     */
    public List<Action> createActions(String key, Collection<Player> players) {
        final List<Action> actions = Lists.newArrayListWithCapacity(players.size());

        for (final Player player : players) {
            actions.add(getActionForPlayer(player));
        }

        setCache(key, actions);

        return actions;
    }

    /**
     * @param key the cache key.
     * @return List of revertable actions stored for the key
     */
    public List<Action> getLastActions(String key) {
        return ImmutableList.copyOf(cache.get(key));
    }

    private void setCache(String actor, List<Action> toRevert) {
        BukkitRunnable task = removeTasks.get(actor);

        if (task != null) {
            task.cancel();
        }

        task = new RemoveTask(actor);
        task.runTaskLater(plugin, cacheTicks);

        cache.replaceValues(actor, toRevert);
        removeTasks.put(actor, task);
    }

    final class RemoveTask extends BukkitRunnable {

        protected final String key;

        private RemoveTask(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            cache.removeAll(key);
            removeTasks.remove(key);
        }
    }
}
