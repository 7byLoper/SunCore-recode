package ru.loper.suncore.commands.core.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.command.BuildableCommand;
import ru.loper.suncore.api.command.register.SubCommandRegister;
import ru.loper.suncore.api.itemstack.ItemBuilder;
import ru.loper.suncore.config.CoreConfigManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SubCommandRegister(permission = "suncore.command.give", aliases = "give")
public class GiveSubCommand implements BuildableCommand {
    private final CoreConfigManager configManager;

    @Override
    public void handle(@NotNull CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(configManager.getMessageConfig().getGiveUsage());
            return;
        }

        ItemBuilder itemBuilder = configManager.getCustomItem(args[1]);
        if (itemBuilder == null) {
            sender.sendMessage(configManager.getMessageConfig().getGiveItemNotFound());
            return;
        }

        Player player = resolveTargetPlayer(sender, args);
        if (player == null) return;

        int amount = resolveAmount(args);
        if (amount <= 0) {
            sender.sendMessage(configManager.getMessageConfig().getGiveInvalidAmount());
            return;
        }

        player.getInventory().addItem(itemBuilder.amount(amount).build());
        sender.sendMessage(String.format(
                configManager.getMessageConfig().getGiveSuccess(),
                args[1], player.getName(), amount
        ));
    }

    private Player resolveTargetPlayer(CommandSender sender, String[] args) {
        if (args.length < 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(configManager.getMessageConfig().getGivePlayerOnly());
                return null;
            }
            return (Player) sender;
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            sender.sendMessage(configManager.getMessageConfig().getGivePlayerNotFound());
            return null;
        }
        return player;
    }

    private int resolveAmount(String[] args) {
        if (args.length < 4) return 1;

        try {
            return Math.max(1, Integer.parseInt(args[3]));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return configManager.getCustomItems().keySet()
                    .stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (args.length == 4) {
            return Stream.of("1", "8", "16", "32", "64")
                    .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}