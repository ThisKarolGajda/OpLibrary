package me.opkarol.oplibrary.inventories;

import me.opkarol.oplibrary.listeners.BasicListener;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InventoryListener extends BasicListener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof ChestInventory.InventoryHolder holder)) {
            return;
        }

        if (event.getClick().isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP || event.getClick() == ClickType.CREATIVE || event.getClick().isCreativeAction()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }

        Map<Integer, AbstractInventory.InteractableItem> actions = holder.slotsActions();
        if (actions == null) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }

        AbstractInventory.InteractableItem item = actions.get(event.getSlot());
        if (item == null) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }

        item.action().accept(event);
    }
}
