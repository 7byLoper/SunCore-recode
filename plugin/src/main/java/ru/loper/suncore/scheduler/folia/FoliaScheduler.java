package ru.loper.suncore.scheduler.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.scheduler.CoreScheduler;
import ru.loper.suncore.api.scheduler.CoreTask;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class FoliaScheduler implements CoreScheduler {
    private final Set<CoreTask> tasks = ConcurrentHashMap.newKeySet();

    @Override
    public CoreTask runSync(Plugin plugin, Runnable task) {
        ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler().run(plugin, st -> task.run());
        return track(new FoliaCoreTask(scheduledTask));
    }

    @Override
    public CoreTask runSyncLater(Plugin plugin, Runnable task, long delayTicks) {
        long delay = Math.max(1L, delayTicks);
        ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, st -> task.run(), delay);
        return track(new FoliaCoreTask(scheduledTask));
    }

    @Override
    public CoreTask runSyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        long delay = Math.max(1L, delayTicks);
        long period = Math.max(1L, periodTicks);
        ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, st -> task.run(), delay, period);
        return track(new FoliaCoreTask(scheduledTask));
    }

    @Override
    public CoreTask runAsync(Plugin plugin, Runnable task) {
        ScheduledTask scheduledTask = Bukkit.getAsyncScheduler().runNow(plugin, st -> task.run());
        return track(new FoliaCoreTask(scheduledTask));
    }

    @Override
    public CoreTask runAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
        long delayMs = Math.max(1L, delayTicks * 50L);
        ScheduledTask scheduledTask = Bukkit.getAsyncScheduler().runDelayed(plugin, st -> task.run(), delayMs, TimeUnit.MILLISECONDS);
        return track(new FoliaCoreTask(scheduledTask));
    }

    @Override
    public CoreTask runAsyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        long delayMs = delayTicks * 50L;
        long periodMs = Math.max(1L, periodTicks * 50L);
        ScheduledTask scheduledTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, st -> task.run(), delayMs, periodMs, TimeUnit.MILLISECONDS);
        return track(new FoliaCoreTask(scheduledTask));
    }

    @Override
    public void shutdown() {
        for (CoreTask coreTask : tasks) {
            try {
                coreTask.cancel();
            } catch (Throwable ignored) {
            }
        }

        tasks.clear();
    }

    private CoreTask track(CoreTask task) {
        tasks.add(task);
        return task;
    }
}