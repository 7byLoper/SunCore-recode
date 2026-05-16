package ru.loper.suncore.commands.core;

import ru.loper.suncore.SunCore;
import ru.loper.suncore.api.command.executor.BaseCommandExecutor;
import ru.loper.suncore.api.command.register.CommandRegister;
import ru.loper.suncore.commands.core.subcommands.GiveAllFromFileSubCommand;
import ru.loper.suncore.commands.core.subcommands.GiveSubCommand;
import ru.loper.suncore.commands.core.subcommands.ReloadSubCommand;
import ru.loper.suncore.commands.core.subcommands.SaveSubCommand;
import ru.loper.suncore.config.CoreConfigManager;

@CommandRegister(name = "suncore", permission = "suncore.command.use", aliases = "score")
public class CoreCommand extends BaseCommandExecutor {
    private final CoreConfigManager configManager;

    public CoreCommand(SunCore plugin) {
        super(plugin);
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public String getNoPermissionMessage() {
        return configManager.getMessageConfig().getNoPermission();
    }

    @Override
    public void registerWrappers() {
        addSubCommand(new ReloadSubCommand(configManager));
        addSubCommand(new SaveSubCommand(configManager));
        addSubCommand(new GiveSubCommand(configManager));
        addSubCommand(new GiveAllFromFileSubCommand(configManager, SunCore.getPlugin(SunCore.class)));
    }
}