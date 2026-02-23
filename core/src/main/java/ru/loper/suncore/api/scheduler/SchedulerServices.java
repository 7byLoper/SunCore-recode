package ru.loper.suncore.api.scheduler;

import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SchedulerServices {
    @Setter
    private volatile CoreScheduler coreScheduler;

    public CoreScheduler clientScheduler() {
        CoreScheduler s = coreScheduler;
        if (s == null) {
            throw new IllegalStateException("CoreScheduler is not set");
        }

        return s;
    }
}
