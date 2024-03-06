package me.opkarol.oplibrary.runnable;

import me.opkarol.oplibrary.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class OpRunnable implements Serializable {
    private int taskId;
    private final BukkitRunnable bukkitRunnable;

    public OpRunnable(Consumer<OpRunnable> consumer) {
        bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(OpRunnable.this);
            }
        };
    }

    public OpRunnable(Runnable runnable) {
        this(runnable1 -> runnable.run());
    }

    public void cancelTask() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @NotNull
    public synchronized OpRunnable runTaskLaterAsynchronously(long delay) throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTaskLaterAsynchronously(Plugin.getInstance(), delay).getTaskId();
        return this;
    }

    @NotNull
    public synchronized OpRunnable runTask() throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTask(Plugin.getInstance()).getTaskId();
        return this;
    }

    @NotNull
    public synchronized OpRunnable runTaskLater(long delay) throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTaskLater(Plugin.getInstance(), delay).getTaskId();
        return this;
    }

    @NotNull
    public synchronized OpRunnable runTaskTimerAsynchronously(long delay, long period) throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTaskTimerAsynchronously(Plugin.getInstance(), delay, period).getTaskId();
        return this;
    }

    @NotNull
    public synchronized OpRunnable runTaskTimerAsynchronously(long delay) throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTaskTimerAsynchronously(Plugin.getInstance(), delay, delay).getTaskId();
        return this;
    }

    @NotNull
    public synchronized OpRunnable runTaskTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTaskTimer(Plugin.getInstance(), delay, period).getTaskId();
        return this;
    }

    @NotNull
    public synchronized OpRunnable runTaskTimer(long delay) throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTaskTimer(Plugin.getInstance(), delay, delay).getTaskId();
        return this;
    }

    @NotNull
    public synchronized OpRunnable runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
        this.taskId = bukkitRunnable.runTaskAsynchronously(Plugin.getInstance()).getTaskId();
        return this;
    }
}
