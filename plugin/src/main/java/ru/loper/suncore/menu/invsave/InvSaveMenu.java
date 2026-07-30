package ru.loper.suncore.menu.invsave;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.loper.suncore.api.menu.impl.AbstractMenu;
import ru.loper.suncore.config.settings.MessagesSettings;
import ru.loper.suncore.manager.SavedInventoryManager;

@RequiredArgsConstructor
public class InvSaveMenu extends AbstractMenu {
    private final String inventoryName;
    private final ItemStack[] initialContents;
    private final SavedInventoryManager inventoryManager;
    private final MessagesSettings messageConfig;

    private boolean saved;

    @Override
    public @Nullable String getTitle() {
        return format(messageConfig.getInvSaveMenuTitle(), "{name}", inventoryName);
    }

    @Override
    public int getSize() {
        return SavedInventoryManager.INVENTORY_SIZE;
    }

    @Override
    public void getItemsAndButtons() {
        int length = Math.min(initialContents.length, getSize());

        for (int slot = 0; slot < length; slot++) {
            ItemStack itemStack = initialContents[slot];

            if (itemStack != null && !itemStack.getType().isAir()) {
                menuItems.put(slot, itemStack.clone());
            }
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(false);
    }

    @Override
    public void onBottomInventoryClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(false);
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(false);
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        if (saved) {
            return;
        }

        saved = true;
        boolean success = inventoryManager.save(inventoryName, event.getInventory().getContents());

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        String message = success
                ? messageConfig.getInvSaveSaved()
                : messageConfig.getInvSaveOperationError();

        player.sendMessage(format(message, "{name}", inventoryName));
    }

    private String format(String message, String... replacements) {
        String result = message;

        for (int index = 0; index + 1 < replacements.length; index += 2) {
            result = result.replace(replacements[index], replacements[index + 1]);
        }

        return result;
    }
}
