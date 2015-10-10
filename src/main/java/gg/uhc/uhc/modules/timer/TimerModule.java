package gg.uhc.uhc.modules.timer;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import gg.uhc.uhc.modules.Module;
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
        messenger = new ActionBarMessenger(ProtocolLibrary.getProtocolManager());

        this.icon.setDisplayName(ChatColor.GREEN + "Actionbar Timer");
        this.icon.setType(Material.WATCH);
        this.icon.setWeight(100);
        this.icon.setLore("Handles running timers in the action bar", "Not disableable");
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
