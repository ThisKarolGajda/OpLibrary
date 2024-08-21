package me.opkarol.oplibrary.injection.inventories.items;

import me.opkarol.oplibrary.injection.messages.StringMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public record ItemClick(InventoryClickEvent event) {

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

    public void sendMessage(StringMessage message) {
        message.send(getPlayer());
    }
}
