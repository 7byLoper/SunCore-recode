package ru.loper.suncore.api.itemstack;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemStackServices {
    @Setter
    private volatile ItemStack skullHead;
    @Setter
    private volatile NamespacedKey placedKey;

    public ItemStack skullHead() {
        ItemStack h = skullHead;
        if (h == null) {
            throw new IllegalStateException("head item stack is not set");
        }

        return h.clone();
    }

    public NamespacedKey placedKey() {
        NamespacedKey p = placedKey;
        if (p == null) {
            throw new IllegalStateException("head item stack is not set");
        }

        return p;
    }
}
