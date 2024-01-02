package me.opkarol.oplibrary.inventories;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SecureItem {
    private static final String safeItemKey = "oplibrary-safeitem-key";

    public static ItemStack applySafeItem(ItemStack item) {
        return new ItemBuilder(item).applyPDC(safeItemKey, UUID.randomUUID().toString());
    }
}
