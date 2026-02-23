package ru.loper.suncore.scheduler.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.loper.suncore.api.scheduler.CoreScheduler;
import ru.loper.suncore.api.scheduler.CoreTask;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitScheduler implements CoreScheduler {
    private final Set<CoreTask> tasks = ConcurrentHashMap.newKeySet();

    @Override
    public CoreTask runSync(Plugin plugin, Runnable task) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTask(plugin, task);
        return track(new BukkitCoreTask(bukkitTask));
    }

    @Override
    public CoreTask runSyncLater(Plugin plugin, Runnable task, long delayTicks) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        return track(new BukkitCoreTask(bukkitTask));
    }

    @Override
    public CoreTask runSyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        return track(new BukkitCoreTask(bukkitTask));
    }

    @Override
    public CoreTask runAsync(Plugin plugin, Runnable task) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        return track(new BukkitCoreTask(bukkitTask));
    }

    @Override
    public CoreTask runAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        return track(new BukkitCoreTask(bukkitTask));
    }

    @Override
    public CoreTask runAsyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
        return track(new BukkitCoreTask(bukkitTask));
    }

    @Override
    public void shutdown() {
        for (CoreTask bukkitTask : tasks) {
            try {
                bukkitTask.cancel();
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