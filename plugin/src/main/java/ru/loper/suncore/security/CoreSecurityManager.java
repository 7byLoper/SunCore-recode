package ru.loper.suncore.security;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.config.settings.SecuritySettings;
import ru.loper.suncore.security.model.SecurityAction;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("removal")
public class CoreSecurityManager extends SecurityManager {
    private final JavaPlugin plugin;

    private final CoreConfigManager configManager;
    private final SecurityManager parentManager;
    private final TelegramNotifier telegramNotifier;

    public CoreSecurityManager(JavaPlugin plugin, CoreConfigManager configManager, SecurityManager parentManager, TelegramNotifier telegramNotifier) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.parentManager = parentManager;
        this.telegramNotifier = telegramNotifier;
    }

    @Override
    public void checkPermission(Permission perm) {
        if (parentManager != null) {
            parentManager.checkPermission(perm);
        }
    }

    @Override
    public void checkWrite(String file) {
        evaluateAction(SecurityAction.FILE_WRITE, file);
        if (parentManager != null) {
            parentManager.checkWrite(file);
        }
    }

    @Override
    public void checkConnect(String host, int port) {
        if (!configManager.getSecuritySettings().getIgnoreNetworkPorts().contains(port)) {
            evaluateAction(SecurityAction.NETWORK_OUT, host + ":" + port);
        }

        if (parentManager != null) {
            parentManager.checkConnect(host, port);
        }
    }

    private void evaluateAction(SecurityAction action, String context) {
        SecuritySettings settings = configManager.getSecuritySettings();

        if (!settings.isEnable() || !settings.isCheckEnable(action.name())) {
            return;
        }

        getCallerPlugin().ifPresent(pluginName -> {
            if (pluginName.equals(plugin.getName())) {
                return;
            }

            if (action == SecurityAction.FILE_WRITE && isWritingToOwnDirectory(pluginName, context)) {
                return;
            }

            List<String> rules = settings.getPermissions().getOrDefault(pluginName.toLowerCase(), List.of());

            if (!rules.contains("ALL") && !rules.contains(action.name())) {
                handleViolation(pluginName, action, context, settings);
            }
        });
    }

    private boolean isWritingToOwnDirectory(String pluginName, String filePath) {
        try {
            Path targetPath = Paths.get(filePath).toAbsolutePath().normalize();
            Path pluginsDir = plugin.getDataFolder().getParentFile().toPath().toAbsolutePath().normalize();
            Path allowedDir = pluginsDir.resolve(pluginName);

            return targetPath.startsWith(allowedDir);
        } catch (Exception e) {
            return false;
        }
    }

    private void handleViolation(String pluginName, SecurityAction action, String context, SecuritySettings settings) {
        String message = settings.getBlockMessage()
                .replace("{plugin-name}", pluginName)
                .replace("{action}", action.name())
                .replace("{context}", context);

        Bukkit.getConsoleSender().sendMessage(message);

        if (settings.isEnableTelegram()) {
            String telegramMessage = settings.getTelegramBlockedMessage()
                    .replace("{plugin-name}", pluginName)
                    .replace("{action}", action.name())
                    .replace("{context}", context);
            telegramNotifier.sendMessage(telegramMessage);
        }

        throw new SecurityException("Action blocked by SunCore Security: " + action.name());
    }

    private Optional<String> getCallerPlugin() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stream -> stream
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .filter(clazz -> clazz.getClassLoader() != null)
                        .filter(clazz -> clazz.getClassLoader().getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader"))
                        .map(clazz -> {
                            try {
                                return JavaPlugin.getProvidingPlugin(clazz).getName();
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(name -> name != null && !name.equals(plugin.getName()))
                        .findFirst()
                );
    }
}