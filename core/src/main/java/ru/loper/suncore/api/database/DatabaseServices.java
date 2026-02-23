package ru.loper.suncore.api.database;

import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class DatabaseServices {
    @Setter
    private volatile DatabaseManager defaultDatabase;
    @Setter
    private volatile boolean replaceAllDatabases = false;

    public DatabaseManager defaultDatabase() {
        return defaultDatabase;
    }

    public boolean replaceAllDatabases() {
        return replaceAllDatabases;
    }
}