package ru.loper.suncore.config;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.database.DatabaseManager;
import ru.loper.suncore.api.itemstack.ItemBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class CoreConfigManager extends ConfigManager {
    private DatabaseManager defaultDatabase;
    private boolean replaceAllDatabases;

    private Map<String, ItemBuilder> customItems;
    private MessageConfig messageConfig;

    private ServerMessage advancementMessage;
    private ServerMessage joinMessage;
    private ServerMessage quitMessage;
    private ServerMessage deathMessage;

    private Set<String> noPhysicsWorlds;

    public CoreConfigManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfigs() {
        plugin.saveDefaultConfig();

        addCustomConfig(new CustomConfig("modules/custom-items.yml", plugin));
        addCustomConfig(new CustomConfig("modules/translation.yml", plugin));
        addCustomConfig(new CustomConfig("modules/server-messages.yml", plugin));
    }

    @Override
    public void loadValues() {
        defaultDatabase = DatabaseManager.fromSection(plugin.getConfig().getConfigurationSection("database"), plugin, false);
        replaceAllDatabases = plugin.getConfig().getBoolean("replace_all_databases");

        customItems = new HashMap<>();
        loadCustomItems();

        messageConfig = new MessageConfig(getTranslationConfig());

        ConfigurationSection serverMessagesConfig = getCustomConfig("modules/server-messages.yml").getConfig();

        advancementMessage = ServerMessage.fromSection(serverMessagesConfig.getConfigurationSection("advancement"));
        joinMessage = ServerMessage.fromSection(serverMessagesConfig.getConfigurationSection("join"));
        quitMessage = ServerMessage.fromSection(serverMessagesConfig.getConfigurationSection("quit"));
        deathMessage = ServerMessage.fromSection(serverMessagesConfig.getConfigurationSection("death"));

        noPhysicsWorlds = new HashSet<>();
        noPhysicsWorlds.addAll(plugin.getConfig().getStringList("no_physics_worlds"));
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

    public boolean isPhysicsDisabled(World world) {
        return world != null && noPhysicsWorlds.contains(world.getName());
    }

    public CustomConfig getCustomItemsConfig() {
        return getCustomConfig("modules/custom-items.yml");
    }

    public CustomConfig getTranslationConfig() {
        return getCustomConfig("modules/translation.yml");
    }
}
