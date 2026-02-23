package ru.loper.suncore.api.config;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class RedisConfig {
    private final String host;
    private final int port;
    private final String password;
    private final long timeout;

    public static RedisConfig fromSection(ConfigurationSection section) {
        final String host = section.getString("host", "127.0.0.1");
        final int port = section.getInt("port", 6379);
        final String password = section.getString("password", "");
        final long timeout = section.getLong("timeout", 3000);

        return new RedisConfig(host, port, password, timeout);
    }
}
