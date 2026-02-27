package ru.loper.suncore.api.scheduler;

import org.bukkit.plugin.Plugin;

public interface CoreScheduler {
    CoreTask runTask(Plugin plugin, Runnable task);

    CoreTask runTaskLater(Plugin plugin, Runnable task, long delayTicks);

    CoreTask runTaskTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks);

    CoreTask runTaskAsynchronously(Plugin plugin, Runnable task);

    CoreTask runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delayTicks);

    CoreTask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delayTicks, long periodTicks);

    void shutdown();
}
