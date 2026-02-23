package ru.loper.suncore.scheduler.bukkit;

import org.bukkit.scheduler.BukkitTask;
import ru.loper.suncore.api.scheduler.CoreTask;

public class BukkitCoreTask implements CoreTask {
    private final BukkitTask task;
    private volatile boolean cancelled;

    public BukkitCoreTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        cancelled = true;
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return cancelled || task.isCancelled();
    }
}