package ru.loper.suncore.registrar;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.command.CommandRegistrar;

import java.lang.reflect.Constructor;
import java.util.List;

public class BukkitCommandRegistrar implements CommandRegistrar {
    @Override
    public void registerCommand(String commandName, CommandExecutor executor, Plugin plugin) {
        this.registerCommand(commandName, List.of(), executor, plugin);
    }

    @Override
    public void registerCommand(String commandName, List<String> aliases, CommandExecutor executor, Plugin plugin) {
        try {
            CommandMap commandMap = Bukkit.getServer().getCommandMap();
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand command = constructor.newInstance(commandName, plugin);

            command.setExecutor(executor);
            command.setAliases(aliases);
            commandMap.register(plugin.getDescription().getName(), command);

            plugin.getLogger().info("The '/%s' command has been successfully registered".formatted(commandName));
        } catch (Exception exception) {
            plugin.getLogger().severe("Unable to register command: " + commandName + ". Error: " + exception.getMessage());
        }

    }
}
