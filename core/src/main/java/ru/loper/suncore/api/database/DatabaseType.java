package ru.loper.suncore.api.database;

import java.util.Arrays;

public enum DatabaseType {
    MYSQL {
        @Override
        public String generateUrl(String host, int port, String table) {
            return "jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true".formatted(host, port, table);
        }
    },
    SQLITE {
        @Override
        public String generateUrl(String host, int port, String table) {
            return "jdbc:sqlite:%s".formatted(table);
        }
    };

    public static DatabaseType getByName(String value, DatabaseType def) {
        if (value == null || value.isBlank()) {
            return def;
        }

        String normalized = value.trim().replace('-', '_').toUpperCase();

        return Arrays.stream(values())
                .filter(v -> v.name().equals(normalized))
                .findFirst()
                .orElse(def);
    }

    public abstract String generateUrl(String host, int port, String table);
}
