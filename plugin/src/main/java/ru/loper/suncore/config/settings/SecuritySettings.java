package ru.loper.suncore.config.settings;

import lombok.Data;
import lombok.NonNull;
import ru.loper.suncore.api.config.CustomConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class SecuritySettings {
    private final boolean enable;
    private final boolean allowCommand;
    private final String blockMessage;

    private final boolean stopIfDisable;

    private final boolean enableTelegram;
    private final String telegramToken;
    private final String telegramChatId;

    private final String telegramBlockedMessage;
    private final String telegramAddRuleMessage;

    private final Map<String, Boolean> checks;
    private final List<Integer> ignoreNetworkPorts;
    private final Map<String, List<String>> permissions;

    public static SecuritySettings loadFromConfig(@NonNull CustomConfig config) {
        final boolean enable = config.getConfig().getBoolean("enable");
        final boolean allowCommand = config.getConfig().getBoolean("allowCommand");
        final String blockMessage = config.configMessage("block_message");

        final boolean stopIfDisable = config.getConfig().getBoolean("stop_if_diable");

        final boolean enableTelegram = config.getConfig().getBoolean("telegram.enable");
        final String telegramToken = config.getConfig().getString("telegram.token");
        final String telegramChatId = config.getConfig().getString("telegram.chat_id");

        final String telegramBlockedMessage = config.getConfig().getString("telegram.messages.blocked");
        final String telegramAddRuleMessage = config.getConfig().getString("telegram.messages.add_rule");

        final List<Integer> ignoreNetworkPorts = config.getConfig().getIntegerList("ignore_network_ports");

        final Map<String, Boolean> checks = new HashMap<>();
        Optional.ofNullable(config.getConfig().getConfigurationSection("checks"))
                .ifPresent(section -> section.getKeys(false).forEach(key ->
                        checks.put(key.toLowerCase(), section.getBoolean(key))
                ));

        final Map<String, List<String>> permissions = new HashMap<>();

        Optional.ofNullable(config.getConfig().getConfigurationSection("permissions"))
                .ifPresent(section -> section.getKeys(false).forEach(key ->
                        permissions.put(key.toLowerCase(), section.getStringList(key))
                ));

        return new SecuritySettings(
                enable, allowCommand, blockMessage, stopIfDisable, enableTelegram,
                telegramToken, telegramChatId, telegramBlockedMessage, telegramAddRuleMessage,
                checks, ignoreNetworkPorts, permissions
        );
    }

    public boolean isCheckEnable(String name) {
        return checks.getOrDefault(name.toLowerCase(), false);
    }
}