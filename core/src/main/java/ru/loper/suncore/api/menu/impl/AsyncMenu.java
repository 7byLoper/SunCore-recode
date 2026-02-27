package ru.loper.suncore.api.menu.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.api.CoreService;
import ru.loper.suncore.api.scheduler.SchedulerServices;

import java.util.concurrent.CompletableFuture;

public abstract class AsyncMenu extends AbstractMenu {
    public void show(@NotNull Player player) {
        opennerPlayer = player;
        menuInventory = createInventory();

        async(() -> {
            this.populateInventory();
            SchedulerServices.clientScheduler().runTaskLater(CoreService.coreInstance(), () -> player.openInventory(menuInventory), 1L);
        });
    }

    protected void populateInventory() {
        menuInventory.clear();
        menuButtons.clear();
        menuItems.clear();

        getItemsAndButtons();
        sync(this::setInventoryItems);
    }

    private void async(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    private void sync(Runnable runnable) {
        SchedulerServices.clientScheduler().runTask(CoreService.coreInstance(), runnable);
    }
}