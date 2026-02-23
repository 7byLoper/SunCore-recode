package ru.loper.suncore.commands.core;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.api.command.executor.BaseCommandExecutor;
import ru.loper.suncore.api.command.register.CommandRegister;
import ru.loper.suncore.commands.core.subcommands.GiveSubCommand;
import ru.loper.suncore.commands.core.subcommands.ReloadSubCommand;
import ru.loper.suncore.commands.core.subcommands.SaveSubCommand;
import ru.loper.suncore.config.CoreConfigManager;

@CommandRegister(name = "suncore", permission = "suncore.command.use")
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
    }

    @Override
    public void handleNoArguments(@NotNull CommandSender sender) {
    }
}