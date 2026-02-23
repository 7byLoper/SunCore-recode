package ru.loper.suncore.api.config;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.colorize.StringColorize;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Data
public class CustomConfig {
    private final File file;
    private final String name;
    private final boolean dataConfig;

    private FileConfiguration config;
    private boolean autoSave = false;
    private boolean colorizeMessages = true;

    public CustomConfig(@NonNull String name, @NonNull Plugin plugin) {
        this(name, false, plugin);
    }

    public CustomConfig(@NonNull String name, boolean dataConfig, @NonNull Plugin plugin) {
        this(name, dataConfig, plugin, plugin.getDataFolder());
    }

    public CustomConfig(@NonNull String name, boolean dataConfig, @NonNull Plugin plugin, @NonNull File directory) {
        this.name = name;
        this.dataConfig = dataConfig;
        this.file = new File(directory, getFileName(name));

        initializeConfig(plugin);
    }

    public CustomConfig(@NonNull File file) {
        this(file, false);
    }

    public CustomConfig(@NonNull File file, boolean autoSave) {
        this.file = file;
        this.autoSave = autoSave;

        dataConfig = true;
        name = file.getName();
        config = YamlConfiguration.loadConfiguration(file);
    }

    private String getFileName(String name) {
        return name.endsWith(".yml") ? name : name + ".yml";
    }

    private void initializeConfig(Plugin plugin) {
        if (!file.exists()) {
            try {
                if (!dataConfig) {
                    plugin.saveResource(getFileName(name), false);
                } else {
                    file.getParentFile().mkdirs();
                    if (file.createNewFile()) {
                        Bukkit.getLogger().log(Level.INFO, "Конфиг {0} успешно создан", file.getName());
                    }
                }
            } catch (IOException | IllegalArgumentException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Ошибка загрузки конфига " + name, e);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Ошибка сохранения конфига " + name, e);
        }
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Ошибка чтения конфига {0}: {1}",
                    new Object[]{name, e.getMessage()});
        } catch (InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Некорректный формат конфига {0}: {1}",
                    new Object[]{name, e.getMessage()});
        }
    }

    public String configMessage(String path) {
        return configMessage(path, "unknown config path: " + path);
    }

    public String configMessage(String path, String defaultValue) {
        String message = config.getString(path, defaultValue);
        return colorizeMessages ? StringColorize.parse(message) : message;
    }

    public void setAndSave(String path, Object value) {
        config.set(path, value);
        if (autoSave) {
            saveConfig();
        }
    }

    boolean getBoolean(String path, boolean defaultValue) {
        if (!config.contains(path)) {
            setAndSave(path, defaultValue);
            return defaultValue;
        }

        return config.getBoolean(path);
    }

    public long getLong(String path, long defaultValue) {
        if (!config.contains(path)) {
            setAndSave(path, defaultValue);
            return defaultValue;
        }

        return config.getLong(path);
    }

    public int getInt(String path, int defaultValue) {
        if (!config.contains(path)) {
            setAndSave(path, defaultValue);
            return defaultValue;
        }

        return config.getInt(path);
    }

    public double getDouble(String path, double defaultValue) {
        if (!config.contains(path)) {
            setAndSave(path, defaultValue);
            return defaultValue;
        }

        return config.getDouble(path);
    }

    public String getString(String path, String defaultValue) {
        if (!config.contains(path)) {
            setAndSave(path, defaultValue);
            return defaultValue;
        }

        return config.getString(path);
    }
}