package ru.loper.suncore.commands.core.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.command.BuildableCommand;
import ru.loper.suncore.api.command.register.SubCommandRegister;
import ru.loper.suncore.api.itemstack.ItemBuilder;
import ru.loper.suncore.config.CoreConfigManager;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@SubCommandRegister(permission = "suncore.command.giveall", aliases = "giveall")
public class GiveAllFromFileSubCommand implements BuildableCommand {
    private final CoreConfigManager configManager;
    private final Plugin plugin;

    @Override
    public void handle(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(configManager.getMessagesSettings().getGiveUsage());
            return;
        }

        Player player = resolveTargetPlayer(commandSender, args);
        if (player == null) return;

        File file = new File(plugin.getDataFolder(), args[1]);
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.getKeys(false).stream()
                .map(config::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(ItemBuilder::fromConfig)
                .forEach(item -> player.getInventory().addItem(item.build()).values()
                        .forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack)));
    }

    private Player resolveTargetPlayer(CommandSender sender, String[] args) {
        if (args.length < 3) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(configManager.getMessagesSettings().getGivePlayerOnly());
                return null;
            }

            return player;
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            sender.sendMessage(configManager.getMessagesSettings().getGivePlayerNotFound());
            return null;
        }

        return player;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length == 2) {
            File folder = plugin.getDataFolder();
            String[] files = folder.list((dir, name) -> name.endsWith(".yml"));
            return files == null ? Collections.emptyList() : List.of(files);
        }

        if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}