package ru.loper.suncore.commands.core.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.command.BuildableCommand;
import ru.loper.suncore.api.command.register.SubCommandRegister;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.config.settings.MessagesSettings;
import ru.loper.suncore.manager.SavedInventoryManager;
import ru.loper.suncore.menu.invsave.InvSaveMenu;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SubCommandRegister(permission = "suncore.command.inv-save", aliases = "inv-save")
public class InvSaveSubCommand implements BuildableCommand {
    private final CoreConfigManager configManager;

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull String[] args) {
        MessagesSettings messages = configManager.getMessagesSettings();

        if (args.length < 2) {
            sender.sendMessage(messages.getInvSaveUsage());
            return;
        }

        String action = args[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "create" -> handleCreate(sender, args);
            case "edit" -> handleEdit(sender, args);
            case "give" -> handleGive(sender, args);
            default -> sender.sendMessage(messages.getInvSaveUnknownAction());
        }
    }

    private void handleCreate(CommandSender sender, String[] args) {
        MessagesSettings messages = configManager.getMessagesSettings();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getInvSaveOnlyPlayer());
            return;
        }

        String name = readInventoryName(sender, args);

        if (name == null) {
            return;
        }

        SavedInventoryManager inventoryManager = configManager.getSavedInventoryManager();

        if (inventoryManager.exists(name)) {
            sender.sendMessage(format(messages.getInvSaveAlreadyExists(), "{name}", name));
            return;
        }

        if (!inventoryManager.create(name)) {
            sender.sendMessage(format(messages.getInvSaveOperationError(), "{name}", name));
            return;
        }

        ItemStack[] contents = inventoryManager.load(name)
                .orElseGet(() -> new ItemStack[SavedInventoryManager.INVENTORY_SIZE]);

        new InvSaveMenu(name, contents, inventoryManager, messages).show(player);
        sender.sendMessage(format(messages.getInvSaveCreateOpened(), "{name}", name));
    }

    private void handleEdit(CommandSender sender, String[] args) {
        MessagesSettings messages = configManager.getMessagesSettings();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getInvSaveOnlyPlayer());
            return;
        }

        String name = readInventoryName(sender, args);

        if (name == null) {
            return;
        }

        SavedInventoryManager inventoryManager = configManager.getSavedInventoryManager();

        if (!inventoryManager.exists(name)) {
            sender.sendMessage(format(messages.getInvSaveNotFound(), "{name}", name));
            return;
        }

        Optional<ItemStack[]> contents = inventoryManager.load(name);

        if (contents.isEmpty()) {
            sender.sendMessage(format(messages.getInvSaveOperationError(), "{name}", name));
            return;
        }

        new InvSaveMenu(name, contents.get(), inventoryManager, messages).show(player);
        sender.sendMessage(format(messages.getInvSaveEditOpened(), "{name}", name));
    }

    private void handleDelete(CommandSender sender, String[] args) {
        MessagesSettings messages = configManager.getMessagesSettings();
        String name = readInventoryName(sender, args);

        if (name == null) {
            return;
        }

        SavedInventoryManager inventoryManager = configManager.getSavedInventoryManager();

        if (!inventoryManager.exists(name)) {
            sender.sendMessage(format(
                    messages.getInvSaveNotFound(),
                    "{name}", name
            ));
            return;
        }

        if (!inventoryManager.delete(name)) {
            sender.sendMessage(format(
                    messages.getInvSaveOperationError(),
                    "{name}", name
            ));
            return;
        }

        sender.sendMessage(format(
                messages.getInvSaveDeleteSuccess(),
                "{name}", name
        ));
    }

    private void handleGive(CommandSender sender, String[] args) {
        MessagesSettings messages = configManager.getMessagesSettings();
        String name = readInventoryName(sender, args);

        if (name == null) {
            return;
        }

        Player target = resolveTarget(sender, args);

        if (target == null) {
            return;
        }

        SavedInventoryManager inventoryManager = configManager.getSavedInventoryManager();

        if (!inventoryManager.exists(name)) {
            sender.sendMessage(format(messages.getInvSaveNotFound(), "{name}", name));
            return;
        }

        Optional<ItemStack[]> contents = inventoryManager.load(name);

        if (contents.isEmpty()) {
            sender.sendMessage(format(messages.getInvSaveOperationError(), "{name}", name));
            return;
        }

        int droppedAmount = inventoryManager.give(target, contents.get());

        sender.sendMessage(format(
                messages.getInvSaveGiveSuccess(),
                "{name}", name,
                "{player}", target.getName()
        ));

        if (droppedAmount > 0) {
            sender.sendMessage(format(
                    messages.getInvSaveDropped(),
                    "{amount}", String.valueOf(droppedAmount),
                    "{player}", target.getName()
            ));
        }
    }

    private String readInventoryName(CommandSender sender, String[] args) {
        MessagesSettings messages = configManager.getMessagesSettings();

        if (args.length < 3) {
            sender.sendMessage(messages.getInvSaveUsage());
            return null;
        }

        SavedInventoryManager inventoryManager = configManager.getSavedInventoryManager();
        String name = inventoryManager.normalizeName(args[2]);

        if (!inventoryManager.isValidName(name)) {
            sender.sendMessage(messages.getInvSaveInvalidName());
            return null;
        }

        return name;
    }

    private Player resolveTarget(CommandSender sender, String[] args) {
        MessagesSettings messages = configManager.getMessagesSettings();

        if (args.length < 4) {
            if (sender instanceof Player player) {
                return player;
            }

            sender.sendMessage(messages.getInvSaveTargetRequired());
            return null;
        }

        Player target = Bukkit.getPlayerExact(args[3]);

        if (target == null) {
            sender.sendMessage(format(
                    messages.getInvSavePlayerNotFound(),
                    "{player}", args[3]
            ));
        }

        return target;
    }

    private String format(String message, String... replacements) {
        String result = message;

        for (int index = 0; index + 1 < replacements.length; index += 2) {
            result = result.replace(replacements[index], replacements[index + 1]);
        }

        return result;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 2) {
            return filter(Stream.of("create", "edit", "give"), args[1]);
        }

        if (args.length == 3 && args.length > 1) {
            String action = args[1].toLowerCase(Locale.ROOT);

            if (action.equals("edit") || action.equals("give")) {
                return filter(
                        configManager.getSavedInventoryManager().getInventoryNames().stream(),
                        args[2]
                );
            }
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("give")) {
            return filter(
                    Bukkit.getOnlinePlayers().stream().map(Player::getName),
                    args[3]
            );
        }

        return Collections.emptyList();
    }

    private List<String> filter(Stream<String> values, String prefix) {
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);

        return values
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
