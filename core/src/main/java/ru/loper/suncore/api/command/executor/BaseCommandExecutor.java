package ru.loper.suncore.api.command.executor;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.loper.suncore.api.command.BuildableCommandWrapper;
import ru.loper.suncore.api.command.CommandServices;
import ru.loper.suncore.api.command.register.CommandRegister;

import java.util.Collections;
import java.util.List;

@Getter
public abstract class BaseCommandExecutor extends SmartCommandExecutor {
    private final String name;
    private final String permission;

    public BaseCommandExecutor(Plugin plugin) {
        this();
        CommandServices.registrar().registerCommand(name, this, plugin);
    }

    public BaseCommandExecutor() {
        CommandRegister register = getClass().getAnnotation(CommandRegister.class);
        if (register == null) {
            throw new IllegalStateException("Аннотация @CommandRegister отсутствует у " + getClass().getName());
        }

        this.name = register.name();
        this.permission = register.permission();
    }

    public BaseCommandExecutor(Plugin plugin, String name, String permission) {
        this.name = name;
        this.permission = permission;

        CommandServices.registrar().registerCommand(name, this, plugin);
    }

    public BaseCommandExecutor(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            sendNoPermissionsMessage(sender);
            return true;
        }

        if (args.length == 0) {
            handleNoArguments(sender);
            return true;
        }

        BuildableCommandWrapper subCommand = getCommandByLabel(args[0]);
        if (subCommand == null) {
            return true;
        }

        if (!sender.hasPermission(subCommand.permission())) {
            sendNoPermissionsMessage(sender);
            return true;
        }

        subCommand.command().handle(sender, args);
        return true;
    }

    private void sendNoPermissionsMessage(@NotNull CommandSender sender) {
        String noPermissionsMessage = getNoPermissionMessage();
        if (noPermissionsMessage == null || noPermissionsMessage.isEmpty()) {
            return;
        }

        sender.sendMessage(noPermissionsMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return getFilteredSubCommandAliases(args[0], sender);
        }

        BuildableCommandWrapper subCommand = getCommandByLabel(args[0]);
        return subCommand == null || !sender.hasPermission(subCommand.permission())
                ? Collections.emptyList()
                : subCommand.command().tabComplete(sender, args);
    }

    public abstract String getNoPermissionMessage();

    public abstract void registerWrappers();

    public abstract void handleNoArguments(@NotNull CommandSender sender);
}
