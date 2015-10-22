/*
 * Project: UHC
 * Class: gg.uhc.uhc.DebouncedRunnable
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DebouncedRunnable {

    protected final Plugin plugin;
    protected final Runnable runnable;
    protected final int timeout;

    protected Optional<Task> task = Optional.absent();
    protected int targetTick;
    protected int currentTick;


    public DebouncedRunnable(Plugin plugin, Runnable runnable, int timeout) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.timeout = timeout;
    }

    public void trigger() {
        if (!task.isPresent()) {
            currentTick = 0;

            // start a new timer
            Task t = new Task();
            t.runTaskTimer(plugin, 0, 1);

            task = Optional.of(t);
        }

        // extend the trigger time
        targetTick = currentTick + timeout;
    }

    class Task extends BukkitRunnable {
        @Override
        public void run() {
            currentTick++;

            if (targetTick <= currentTick) {
                this.cancel();
                task = Optional.absent();
                runnable.run();
            }
        }
    }
}
