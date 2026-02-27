package ru.loper.suncore.api.scheduler;

import org.bukkit.plugin.Plugin;

public abstract class CoreRunnable implements Runnable {
    
    private CoreTask task;
    private volatile boolean cancelled = false;

    @Override
    public abstract void run();

    public synchronized void cancel() {
        this.cancelled = true;
        if (this.task != null) {
            this.task.cancel();
        }
    }

    public synchronized boolean isCancelled() {
        return this.cancelled || (this.task != null && this.task.isCancelled());
    }

    public synchronized CoreTask getTask() {
        if (this.task == null) {
            throw new IllegalStateException("Задача еще не запланирована.");
        }
        
        return this.task;
    }

    public CoreTask runTask(Plugin plugin) {
        return setup(SchedulerServices.clientScheduler().runTask(plugin, this));
    }

    public CoreTask runTaskLater(Plugin plugin, long delayTicks) {
        return setup(SchedulerServices.clientScheduler().runTaskLater(plugin, this, delayTicks));
    }

    public CoreTask runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) {
        return setup(SchedulerServices.clientScheduler().runTaskTimer(plugin, this, delayTicks, periodTicks));
    }

    public CoreTask runTaskAsynchronously(Plugin plugin) {
        return setup(SchedulerServices.clientScheduler().runTaskAsynchronously(plugin, this));
    }

    public CoreTask runTaskLaterAsynchronously(Plugin plugin, long delayTicks) {
        return setup(SchedulerServices.clientScheduler().runTaskLaterAsynchronously(plugin, this, delayTicks));
    }

    public CoreTask runTaskTimerAsynchronously(Plugin plugin, long delayTicks, long periodTicks) {
        return setup(SchedulerServices.clientScheduler().runTaskTimerAsynchronously(plugin, this, delayTicks, periodTicks));
    }

    private synchronized CoreTask setup(CoreTask scheduledTask) {
        if (this.task != null) {
            scheduledTask.cancel();
            throw new IllegalStateException("Этот CoreRunnable уже был запланирован.");
        }
        
        this.task = scheduledTask;
        
        if (this.cancelled) {
            scheduledTask.cancel();
        }
        
        return scheduledTask;
    }
}