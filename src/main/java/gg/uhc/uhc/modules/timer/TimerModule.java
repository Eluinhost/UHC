/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.timer.TimerModule
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

package gg.uhc.uhc.modules.timer;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.ModuleRegistry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerModule extends Module {

    protected final ActionBarMessenger messenger;

    protected Optional<TimerRunnable> currentTimer = Optional.absent();
    protected long currentTick;
    protected long targetTick;

    public TimerModule() {
        setId("Timer");

        messenger = new ActionBarMessenger(ProtocolLibrary.getProtocolManager());

        this.icon.setDisplayName(ChatColor.GREEN + "Actionbar Timer");
        this.icon.setType(Material.WATCH);
        this.icon.setWeight(ModuleRegistry.CATEGORY_MISC);
    }

    @Override
    public void initialize() {
        this.icon.setLore(messages.getRaw("lore"));
    }

    public void startTimer(TimerMessage message, long seconds) {
        Preconditions.checkState(!currentTimer.isPresent(), "There is already a timer in progress");
        Preconditions.checkArgument(seconds > 0, "Timers must be longer than 0 seconds");
        Preconditions.checkNotNull(message, "Message cannot be null");

        // start at -1 so first immediate run is at 0
        currentTick = -1;
        targetTick = seconds;

        // start timer for 1 second loop
        TimerRunnable timer = new TimerRunnable(message);
        timer.runTaskTimer(plugin, 0, 20);
        currentTimer = Optional.of(timer);
    }

    public void extend(long ticks) {
        Preconditions.checkState(currentTimer.isPresent(), "There is no timer in progress");
        Preconditions.checkArgument(ticks > 0, "Must provide a positive value to extend the timer");

        targetTick += ticks;
    }

    public void cancel() {
        Preconditions.checkState(currentTimer.isPresent());

        currentTimer.get().cancel();
        currentTimer = Optional.absent();
    }

    public boolean isRunning() {
        return currentTimer.isPresent();
    }

    protected class TimerRunnable extends BukkitRunnable {

        protected final TimerMessage message;

        public TimerRunnable(TimerMessage message) {
            this.message = message;
        }

        @Override
        public void run() {
            currentTick++;

            messenger.sendMessage(Bukkit.getOnlinePlayers(), message.getMessage(targetTick - currentTick));

            if (currentTick == targetTick) {
                TimerModule.this.cancel();
            }
        }
    }
}
