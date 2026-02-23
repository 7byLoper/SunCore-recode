package ru.loper.suncore.api.scheduler;

import org.bukkit.plugin.Plugin;

public interface CoreScheduler {
    CoreTask runSync(Plugin plugin, Runnable task);

    CoreTask runSyncLater(Plugin plugin, Runnable task, long delayTicks);

    CoreTask runSyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks);

    CoreTask runAsync(Plugin plugin, Runnable task);

    CoreTask runAsyncLater(Plugin plugin, Runnable task, long delayTicks);

    CoreTask runAsyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks);

    void shutdown();
}
