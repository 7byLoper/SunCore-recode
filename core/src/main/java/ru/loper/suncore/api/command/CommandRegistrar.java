package ru.loper.suncore.api.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

public interface CommandRegistrar {
    void registerCommand(String commandName, CommandExecutor executor, Plugin plugin);
}