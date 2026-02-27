package ru.loper.suncore.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;

public record ServerMessage(boolean enable, Component message) {
    public static ServerMessage fromSection(ConfigurationSection section) {
        if (section == null) {
            return new ServerMessage(true, null);
        }
        boolean enable = section.getBoolean("enable");

        String stringMessage = section.getString("message");
        Component message = stringMessage == null || stringMessage.isEmpty() ?
                null :
                LegacyComponentSerializer.legacySection().deserialize(stringMessage);

        return new ServerMessage(enable, message);
    }
}
