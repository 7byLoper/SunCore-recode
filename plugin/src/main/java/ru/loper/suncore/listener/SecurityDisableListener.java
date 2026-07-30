package ru.loper.suncore.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.config.settings.SecuritySettings;

@RequiredArgsConstructor
public class SecurityDisableListener implements Listener {
    private final JavaPlugin plugin;
    private final CoreConfigManager configManager;

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        SecuritySettings settings = configManager.getSecuritySettings();
        
        if (settings.isEnable() && settings.isStopIfDisable() && event.getPlugin().equals(plugin)) {
            Bukkit.shutdown();
        }
    }
}