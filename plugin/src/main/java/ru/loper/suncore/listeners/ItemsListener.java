package ru.loper.suncore.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suncore.api.itemstack.ItemStackServices;

@RequiredArgsConstructor
public class ItemsListener implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(ItemStackServices.placedKey(), PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
