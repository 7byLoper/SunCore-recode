package ru.loper.suncore.api.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DatabaseManager {

    private final Plugin plugin;

    private final String host;
    private final int port;

    private final String username;
    private final String password;
    private final String table;

    private final DatabaseType dataType;
    private final ConnectType connectType;

    private String url;

    private volatile Connection persistentConnection;

    public static DatabaseManager fromSection(ConfigurationSection section, Plugin plugin) {
        return fromSection(section, plugin, false);
    }

    public static DatabaseManager fromSection(ConfigurationSection section, Plugin plugin, boolean replaced) {
        return fromSection(section, plugin, DatabaseServices.defaultDatabase(), replaced);
    }

    public static DatabaseManager fromSection(ConfigurationSection section, Plugin plugin, DatabaseManager defaultManager, boolean replaced) {
        if (section == null) {
            return defaultManager;
        }

        if (DatabaseServices.replaceAllDatabases() && replaced) {
            return DatabaseServices.defaultDatabase();
        }

        String host = section.getString("host", "");
        int port = section.getInt("port", 3306);

        String username = section.getString("username", "");
        String password = section.getString("password", "");
        String table = section.getString("name", "");

        DatabaseType databaseType = DatabaseType.getByName(section.getString("data_type", "MYSQL"), DatabaseType.SQLITE);
        ConnectType connectType = ConnectType.getByName(section.getString("connect_type", "PER_OPERATION"), ConnectType.PER_OPERATION);

        String url = databaseType == DatabaseType.SQLITE
                ? databaseType.generateUrl(host, port, new File(plugin.getDataFolder(), table + ".db").getAbsolutePath())
                : databaseType.generateUrl(host, port, table);

        return new DatabaseManager(plugin, host, port, username, password, table, databaseType, connectType, url, null);
    }

    public Connection getConnection() throws SQLException {
        ensureUrl();

        if (!connectType.isPersistent()) {
            return openNewConnection();
        }

        Connection current = persistentConnection;
        if (isAlive(current)) {
            return current;
        }

        synchronized (this) {
            current = persistentConnection;
            if (isAlive(current)) {
                return current;
            }

            persistentConnection = openNewConnection();
            return persistentConnection;
        }
    }

    public void close() {
        Connection current = persistentConnection;
        if (current == null) {
            return;
        }

        synchronized (this) {
            current = persistentConnection;
            persistentConnection = null;
        }

        try {
            current.close();
        } catch (SQLException ignored) {
        }
    }

    public String getSqlByDataType(String mysql, String sqlite) {
        return dataType == DatabaseType.MYSQL ? mysql : sqlite;
    }

    private void ensureUrl() {
        if (url != null && !url.isBlank()) {
            return;
        }

        if (dataType == DatabaseType.SQLITE) {
            url = dataType.generateUrl(host, port, new File(plugin.getDataFolder(), table + ".db").getAbsolutePath());
            return;
        }

        url = dataType.generateUrl(host, port, table);
    }

    private Connection openNewConnection() throws SQLException {
        return switch (dataType) {
            case MYSQL -> DriverManager.getConnection(url, username, password);
            case SQLITE -> DriverManager.getConnection(url);
        };
    }

    private boolean isAlive(Connection connection) {
        if (connection == null) {
            return false;
        }

        try {
            return !connection.isClosed();
        } catch (SQLException ignored) {
            return false;
        }
    }
}