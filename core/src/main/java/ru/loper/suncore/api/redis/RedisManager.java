package ru.loper.suncore.api.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import ru.loper.suncore.api.config.RedisConfig;

import java.util.HashSet;
import java.util.Set;

@Getter
public class RedisManager {
    private static final Set<RedisManager> instances = new HashSet<>();

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final RedisMessageListener listener;
    private final boolean enabled;

    public RedisManager(RedisConfig config, RedisMessageListener listener, String... channels) {
        this.listener = listener;
        this.enabled = config.isEnable();

        instances.add(this);

        if (!enabled) {
            redisClient = null;
            connection = null;
            pubSubConnection = null;
            return;
        }

        RedisURI redisUri = RedisURI.Builder.redis(config.getHost(), config.getPort())
                .withPassword(config.getPassword().toCharArray())
                .build();

        redisClient = RedisClient.create(redisUri);
        connection = redisClient.connect();
        pubSubConnection = redisClient.connectPubSub();

        pubSubConnection.addListener(listener);
        pubSubConnection.async().subscribe(channels);
    }

    public void onDisable() {
        if (redisClient != null) {
            redisClient.shutdown();
        }

        instances.remove(this);
    }

    public void sendCommand(String channel, String message) {
        if (!enabled) {
            listener.message(channel, message);
            return;
        }

        connection.sync().publish(channel, message);
    }

    public static void shutdownAll() {
        Set.copyOf(instances).forEach(RedisManager::onDisable);
    }
}