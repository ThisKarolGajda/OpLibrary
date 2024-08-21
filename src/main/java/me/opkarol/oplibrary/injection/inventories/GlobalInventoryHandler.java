package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.inventories.items.GlobalItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record GlobalInventoryHandler(List<GlobalItem> globalItemList,
                                     @Nullable Consumer<InventoryClickEvent> clickEventConsumer,
                                     @Nullable Consumer<InventoryDragEvent> dragEventConsumer) implements InventoryHolder {

    @Override
    @SuppressWarnings("all")
    public @Nullable Inventory getInventory() {
        return null;
    }

    public GlobalItem get(int slot) {
        return globalItemList.stream().filter(globalItem -> globalItem.slot() == slot).findFirst().orElse(null);
    }
}
