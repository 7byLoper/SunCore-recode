package ru.loper.suncore.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.database.DatabaseManager;
import ru.loper.suncore.api.itemstack.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CoreConfigManager extends ConfigManager {
    private DatabaseManager defaultDatabase;
    private boolean replaceAllDatabases;

    private Map<String, ItemBuilder> customItems;
    private MessageConfig messageConfig;

    public CoreConfigManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfigs() {
        plugin.saveDefaultConfig();

        addCustomConfig(new CustomConfig("modules/custom-items.yml", plugin));
        addCustomConfig(new CustomConfig("modules/translation.yml", plugin));
    }

    @Override
    public void loadValues() {
        defaultDatabase = DatabaseManager.fromSection(plugin.getConfig().getConfigurationSection("database"), plugin, false);
        replaceAllDatabases = plugin.getConfig().getBoolean("replace_all_databases");

        customItems = new HashMap<>();
        loadCustomItems();

        messageConfig = new MessageConfig(getTranslationConfig());
    }

    private void loadCustomItems() {
        ConfigurationSection itemsSection = getCustomItemsConfig().getConfig();
        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) {
                continue;
            }

            customItems.put(key, ItemBuilder.fromConfig(itemSection));
        }
    }

    public ItemBuilder getCustomItem(String name) {
        if (!customItems.containsKey(name)) {
            return null;
        }

        ItemBuilder itemBuilder = customItems.get(name);
        return new ItemBuilder(itemBuilder.build());
    }

    public void addItem(String name, ItemStack itemStack) {
        ItemBuilder itemBuilder = new ItemBuilder(itemStack);
        customItems.put(name, itemBuilder);

        ConfigurationSection itemSection = getCustomItemsConfig().getConfig().createSection(name);
        itemBuilder.save(itemSection);
        getCustomItemsConfig().saveConfig();
    }

    public CustomConfig getCustomItemsConfig() {
        return getCustomConfig("modules/custom-items.yml");
    }

    public CustomConfig getTranslationConfig() {
        return getCustomConfig("modules/translation.yml");
    }
}
