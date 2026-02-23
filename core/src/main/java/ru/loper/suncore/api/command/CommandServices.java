package ru.loper.suncore.api.command;

import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CommandServices {

    @Setter
    private volatile CommandRegistrar registrar;

    public CommandRegistrar registrar() {
        CommandRegistrar r = registrar;
        if (r == null) {
            throw new IllegalStateException("CommandRegistrar is not set");
        }

        return r;
    }
}