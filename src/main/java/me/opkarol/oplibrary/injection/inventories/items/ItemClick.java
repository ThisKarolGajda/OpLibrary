package me.opkarol.oplibrary.injection.inventories.items;

import me.opkarol.oplibrary.injection.inventories.GlobalInventory;
import me.opkarol.oplibrary.injection.messages.StringMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public record ItemClick(InventoryClickEvent event, GlobalInventory globalInventory) {

    public @NotNull Player getPlayer() {
        return (Player) getEvent().getWhoClicked();
    }

    public int getSlot() {
        return getEvent().getSlot();
    }

    public @NotNull ClickType getType() {
        return getEvent().getClick();
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

    public void cancel() {
        event.setCancelled(true);
    }

    public void sendMessage(@NotNull StringMessage message) {
        message.send(getPlayer());
    }

    public void sendMessage(String message) {
        new StringMessage(message).send(getPlayer());
    }

    public GlobalInventory getInventory() {
        return globalInventory;
    }

    public void open() {
        if (!event.isCancelled()) {
            event.setCancelled(true);
        }

        globalInventory.open(getPlayer());
    }

}
