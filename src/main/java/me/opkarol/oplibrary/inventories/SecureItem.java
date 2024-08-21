package me.opkarol.oplibrary.inventories;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@IgnoreInject
public class SecureItem {
    private static final String safeItemKey = "oplibrary-safeitem-key";

    public static ItemBuilder applySafeItem(ItemStack item) {
        return new ItemBuilder(item).applyPDC(safeItemKey, UUID.randomUUID().toString());
    }

    public static ItemBuilder removeSafeItem(ItemStack item) {
        return new ItemBuilder(item).removePDC(safeItemKey);
    }
}
