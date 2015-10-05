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
