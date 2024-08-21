package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.inventories.items.GlobalItem;
import me.opkarol.oplibrary.injection.inventories.items.ItemClick;
import me.opkarol.oplibrary.listeners.Listener;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class GlobalInventoryListener extends Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !(event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof GlobalInventoryHandler holder)) {
            return;
        }

        if (holder.clickEventConsumer() != null && Objects.equals(event.getClickedInventory(), event.getWhoClicked().getInventory())) {
            holder.clickEventConsumer().accept(event);
        } else {
            if (!(event.getClickedInventory().getHolder() instanceof GlobalInventoryHandler)) {
                isIllegalClick(event);
                return;
            }

            if (holder.clickEventConsumer() != null) {
                holder.clickEventConsumer().accept(event);
            }

            if (isIllegalClick(event)) {
                return;
            }

            List<GlobalItem> items = holder.globalItemList();
            if (items == null) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }

            GlobalItem item = holder.get(event.getSlot());
            if (item == null) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }

            item.consumer().accept(new ItemClick(event));
        }
    }

    private boolean isIllegalClick(@NotNull InventoryClickEvent event) {
        if (event.getClick().isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP || event.getClick() == ClickType.CREATIVE || event.getClick().isCreativeAction()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return true;
        }

        return false;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof GlobalInventoryHandler holder)) {
            return;
        }

        if (holder.dragEventConsumer() != null) {
            holder.dragEventConsumer().accept(event);
        }
    }
}
