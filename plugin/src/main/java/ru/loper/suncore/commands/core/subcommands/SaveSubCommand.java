package ru.loper.suncore.commands.core.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.command.BuildableCommand;
import ru.loper.suncore.api.command.register.SubCommandRegister;
import ru.loper.suncore.config.CoreConfigManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SubCommandRegister(permission = "suncore.command.save", aliases = "save")
public class SaveSubCommand implements BuildableCommand {
    private final CoreConfigManager configManager;

    @Override
    public void handle(@NotNull CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        if (args.length != 2) {
            sender.sendMessage(configManager.getMessageConfig().getSaveUsage());
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            sender.sendMessage(configManager.getMessageConfig().getSaveAir());
            return;
        }

        configManager.addItem(args[1], item);
        sender.sendMessage(configManager.getMessageConfig().getSaveSuccess()
                .replace("{name}", args[1]));
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Stream.of("<название>")
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}