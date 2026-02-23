package ru.loper.suncore.api;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;

@UtilityClass
public class CoreService {
    @Setter
    private volatile Plugin coreInstance;

    public Plugin coreInstance() {
        Plugin s = coreInstance;
        if (s == null) {
            throw new IllegalStateException("CoreInstance is not set");
        }

        return s;
    }
}
