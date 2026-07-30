package ru.loper.suncore.api.database;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides one bounded database executor per plugin.
 */
public final class PluginDatabaseExecutor {
    private static final int WORKER_THREADS = 4;
    private static final int QUEUE_CAPACITY = 1024;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 10L;
    private static final long SHUTDOWN_ALL_TIMEOUT_SECONDS = 30L;

    private static final Map<Plugin, ExecutorService> EXECUTORS = new ConcurrentHashMap<>();

    private PluginDatabaseExecutor() {
    }

    public static ExecutorService get(Class<?> pluginClass) {
        Plugin plugin = JavaPlugin.getProvidingPlugin(pluginClass);
        return EXECUTORS.computeIfAbsent(plugin, PluginDatabaseExecutor::create);
    }

    public static void shutdown(Class<?> pluginClass) {
        shutdown(JavaPlugin.getProvidingPlugin(pluginClass));
    }

    public static void shutdownAll() {
        Collection<ExecutorService> executors = new ArrayList<>(EXECUTORS.values());
        EXECUTORS.clear();

        executors.forEach(ExecutorService::shutdown);

        boolean interrupted = false;
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(SHUTDOWN_ALL_TIMEOUT_SECONDS);

        for (ExecutorService executor : executors) {
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0L) {
                executor.shutdownNow();
                continue;
            }

            try {
                if (!executor.awaitTermination(remaining, TimeUnit.NANOSECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException exception) {
                interrupted = true;
                executor.shutdownNow();
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private static ExecutorService create(Plugin plugin) {
        return new ThreadPoolExecutor(
                WORKER_THREADS,
                WORKER_THREADS,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new DatabaseThreadFactory(plugin.getName()),
                new BackpressurePolicy()
        );
    }

    private static void shutdown(Plugin plugin) {
        ExecutorService executor = EXECUTORS.remove(plugin);
        if (executor == null) {
            return;
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static final class DatabaseThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger counter = new AtomicInteger();

        private DatabaseThreadFactory(String pluginName) {
            this.prefix = pluginName + "-Database-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, prefix + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }

    private static final class BackpressurePolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                throw new RejectedExecutionException("Database executor is shut down");
            }
            runnable.run();
        }
    }
}
