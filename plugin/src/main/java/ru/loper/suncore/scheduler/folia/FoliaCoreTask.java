package ru.loper.suncore.scheduler.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import ru.loper.suncore.api.scheduler.CoreTask;

public final class FoliaCoreTask implements CoreTask {

    private final ScheduledTask task;
    private volatile boolean cancelled;

    public FoliaCoreTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
        this.task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled || this.task.isCancelled();
    }
}