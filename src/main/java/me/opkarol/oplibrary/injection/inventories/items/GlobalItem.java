package me.opkarol.oplibrary.injection.inventories.items;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class GlobalItem {
    private String id;
    private String name;
    private List<String> lore;
    private int slot;
    private ItemStack itemStack;
    private final Consumer<ItemClick> consumer;

    public GlobalItem(String id, String name, List<String> lore, int slot, ItemStack itemStack, Consumer<ItemClick> consumer) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.itemStack = itemStack;
        this.consumer = consumer;
    }

    public GlobalItem(String id, String name, List<String> lore, ItemStack itemStack, Consumer<ItemClick> consumer) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.itemStack = itemStack;
        this.consumer = consumer;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public List<String> lore() {
        return lore;
    }

    public int slot() {
        return slot;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public Consumer<ItemClick> consumer() {
        return consumer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GlobalItem) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.lore, that.lore) &&
                this.slot == that.slot &&
                Objects.equals(this.itemStack, that.itemStack) &&
                Objects.equals(this.consumer, that.consumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lore, slot, itemStack, consumer);
    }

    @Override
    public String toString() {
        return "GlobalItem[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "lore=" + lore + ", " +
                "slot=" + slot + ", " +
                "itemStack=" + itemStack + ", " +
                "consumer=" + consumer + ']';
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
