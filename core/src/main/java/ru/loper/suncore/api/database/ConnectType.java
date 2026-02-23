package ru.loper.suncore.api.database;

import java.util.Arrays;

public enum ConnectType {
    PERSISTENT,
    PER_OPERATION;

    public static ConnectType getByName(String value, ConnectType def) {
        if (value == null || value.isBlank()) {
            return def;
        }

        String normalized = value.trim().replace('-', '_').toUpperCase();

        return Arrays.stream(values())
                .filter(v -> v.name().equals(normalized))
                .findFirst()
                .orElse(def);
    }

    public boolean isPersistent() {
        return this == PERSISTENT;
    }
}
