package ru.loper.suncore.commands.core.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.command.BuildableCommand;
import ru.loper.suncore.api.command.register.SubCommandRegister;
import ru.loper.suncore.config.CoreConfigManager;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@SubCommandRegister(permission = "suncore.command.reload", aliases = "reload")
public class ReloadSubCommand implements BuildableCommand {
    private final CoreConfigManager configManager;

    @Override
    public void handle(@NotNull CommandSender commandSender, @NotNull String[] args) {
        long start = System.currentTimeMillis();
        configManager.reloadAll();
        long stop = System.currentTimeMillis();

        commandSender.sendMessage(configManager.getMessageConfig().getReloadSuccess()
                .replace("{ms}", String.valueOf(stop - start)));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return Collections.emptyList();
    }
}