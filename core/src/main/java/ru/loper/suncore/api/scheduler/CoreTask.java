package ru.loper.suncore.api.scheduler;

public interface CoreTask {
    void cancel();

    boolean isCancelled();
}