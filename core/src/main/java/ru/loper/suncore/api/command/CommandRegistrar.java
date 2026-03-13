package ru.loper.suncore.api.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface CommandRegistrar {
    void registerCommand(String commandName, CommandExecutor executor, Plugin plugin);

    void registerCommand(String commandName, List<String> aliases, CommandExecutor executor, Plugin plugin);
}