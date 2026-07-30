package ru.loper.suncore.commands.core.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.command.BuildableCommand;
import ru.loper.suncore.api.command.register.SubCommandRegister;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.security.TelegramNotifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SubCommandRegister(permission = "suncore.command.security", aliases = "security")
public class SecuritySubCommand implements BuildableCommand {

    private final CoreConfigManager configManager;
    private final TelegramNotifier telegramNotifier;

    @Override
    public void handle(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (!Bukkit.getConsoleSender().equals(commandSender) || !configManager.getSecuritySettings().isAllowCommand()) {
            commandSender.sendMessage("s");
            return;
        }

        if (args.length < 4) {
            commandSender.sendMessage(configManager.getMessagesSettings().getSecurityUsage());
            return;
        }

        if (args[1].equalsIgnoreCase("allow")) {
            allowSecurityRule(commandSender, args);
        }
    }

    private void allowSecurityRule(CommandSender commandSender, String[] args) {
        final String pluginName = args[2];
        final String action = args[3].toUpperCase();

        final CustomConfig securityConfig = configManager.getCustomConfig("modules/security.yml");
        ConfigurationSection permsSection = securityConfig.getConfig().getConfigurationSection("permissions");

        if (permsSection == null) {
            permsSection = securityConfig.getConfig().createSection("permissions");
        }

        List<String> pluginPerms = permsSection.getStringList(pluginName);
        if (pluginPerms.isEmpty()) {
            pluginPerms = new ArrayList<>();
        }

        if (pluginPerms.contains(action)) {
            commandSender.sendMessage(
                    configManager.getMessagesSettings().getSecurityAlreadyAdded()
                            .replace("{plugin}", pluginName)
                            .replace("{action}", action)
            );
            return;
        }

        pluginPerms.add(action);
        permsSection.set(pluginName, pluginPerms);
        securityConfig.saveConfig();
        configManager.loadValues();

        commandSender.sendMessage(
                configManager.getMessagesSettings().getSecurityAdd()
                        .replace("{plugin}", pluginName)
                        .replace("{action}", action)
        );

        if (configManager.getSecuritySettings().isEnableTelegram()) {
            String tgMessage = configManager.getSecuritySettings().getTelegramAddRuleMessage()
                    .replace("{plugin}", pluginName)
                    .replace("{action}", action);
            telegramNotifier.sendMessage(tgMessage);
        }
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("allow")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("allow")) {
            return Stream.of(Bukkit.getPluginManager().getPlugins())
                    .map(Plugin::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("allow")) {
            return Stream.of("ALL", "FILE_WRITE", "NETWORK_OUT", "PLUGIN_LIST_READ")
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }

    @Override
    public boolean hasTabComplete(@NotNull CommandSender commandSender) {
        return commandSender instanceof ConsoleCommandSender;
    }
}