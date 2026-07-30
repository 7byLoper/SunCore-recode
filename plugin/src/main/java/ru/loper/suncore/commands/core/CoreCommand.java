package ru.loper.suncore.commands.core;

import ru.loper.suncore.SunCore;
import ru.loper.suncore.api.command.executor.BaseCommandExecutor;
import ru.loper.suncore.api.command.register.CommandRegister;
import ru.loper.suncore.commands.core.subcommands.*;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.security.TelegramNotifier;

@CommandRegister(name = "suncore", permission = "suncore.command.use", aliases = "score")
public class CoreCommand extends BaseCommandExecutor {
    private final CoreConfigManager configManager;
    private final TelegramNotifier telegramNotifier;

    public CoreCommand(SunCore plugin, TelegramNotifier telegramNotifier) {
        super(plugin);
        this.configManager = plugin.getConfigManager();
        this.telegramNotifier = telegramNotifier;
    }

    @Override
    public String getNoPermissionMessage() {
        return configManager.getMessagesSettings().getNoPermission();
    }

    @Override
    public void registerWrappers() {
        addSubCommand(new ReloadSubCommand(configManager));
        addSubCommand(new SaveSubCommand(configManager));
        addSubCommand(new GiveSubCommand(configManager));
        addSubCommand(new InvSaveSubCommand(configManager));
        addSubCommand(new GiveAllFromFileSubCommand(configManager, SunCore.getPlugin(SunCore.class)));

        addSubCommand(new SecuritySubCommand(configManager, telegramNotifier));
    }
}