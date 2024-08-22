package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.inventories.items.GlobalItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GlobalInventoryHandler implements InventoryHolder {
    private List<GlobalItem> displayedItems = new ArrayList<>();
    private final GlobalInventory globalInventory;
    @Nullable
    private final Consumer<InventoryClickEvent> clickEventConsumer;
    @Nullable
    private final Consumer<InventoryDragEvent> dragEventConsumer;

    public GlobalInventoryHandler(GlobalInventory globalInventory,
                                  @Nullable Consumer<InventoryClickEvent> clickEventConsumer,
                                  @Nullable Consumer<InventoryDragEvent> dragEventConsumer) {
        this.globalInventory = globalInventory;
        this.clickEventConsumer = clickEventConsumer;
        this.dragEventConsumer = dragEventConsumer;
    }

    @Override
    @SuppressWarnings("all")
    public @Nullable Inventory getInventory() {
        return null;
    }

    public GlobalItem get(Player player, int slot) {
        return globalInventory.getItems().stream()
                .filter(globalItem -> globalItem.getSlot() == slot)
                .findFirst()
                .orElse(null);
    }

    public GlobalInventory getGlobalInventory() {
        return globalInventory;
    }

    @Nullable
    public Consumer<InventoryClickEvent> getClickEventConsumer() {
        return clickEventConsumer;
    }

    @Nullable
    public Consumer<InventoryDragEvent> getDragEventConsumer() {
        return dragEventConsumer;
    }

    public void setDisplayedItems(List<GlobalItem> displayedItems) {
        this.displayedItems = displayedItems;
    }

    public List<GlobalItem> getDisplayedItems() {
        return displayedItems;
    }
}