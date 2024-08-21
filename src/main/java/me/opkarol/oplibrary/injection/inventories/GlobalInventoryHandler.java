package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.inventories.items.GlobalItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GlobalInventoryHandler implements InventoryHolder {
    private List<GlobalItem> globalItemList;
    @Nullable
    private final Consumer<InventoryClickEvent> clickEventConsumer;
    @Nullable
    private final Consumer<InventoryDragEvent> dragEventConsumer;

    public GlobalInventoryHandler(List<GlobalItem> globalItemList,
                                  @Nullable Consumer<InventoryClickEvent> clickEventConsumer,
                                  @Nullable Consumer<InventoryDragEvent> dragEventConsumer) {
        this.globalItemList = globalItemList;
        this.clickEventConsumer = clickEventConsumer;
        this.dragEventConsumer = dragEventConsumer;
    }

    @Override
    @SuppressWarnings("all")
    public @Nullable Inventory getInventory() {
        return null;
    }

    public GlobalItem get(Player player, int slot) {
        return globalItemList.stream()
                .filter(globalItem -> globalItem.getSlot() == slot)
                .findFirst()
                .orElse(null);
    }

    public List<GlobalItem> getGlobalItemList() {
        return globalItemList;
    }

    @Nullable
    public Consumer<InventoryClickEvent> getClickEventConsumer() {
        return clickEventConsumer;
    }

    @Nullable
    public Consumer<InventoryDragEvent> getDragEventConsumer() {
        return dragEventConsumer;
    }

    public void setItems(List<GlobalItem> displayedItems) {
        this.globalItemList = displayedItems;
    }
}