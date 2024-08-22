package me.opkarol.oplibrary.injection.inventories.items;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@IgnoreInject
public final class GlobalItem {
    private final String id;
    private String name;
    private List<String> lore;
    private int slot;
    private ItemStack itemStack;
    private Function<Player, Map<String, Object>> replacements;
    private Consumer<ItemClick> consumer;

    public GlobalItem(String id, String name, List<String> lore, int slot, ItemStack itemStack, Function<Player, Map<String, Object>> replacements, Consumer<ItemClick> consumer) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.itemStack = itemStack;
        this.replacements = replacements;
        this.consumer = consumer;
    }

    public GlobalItem(String id, String name, List<String> lore, int slot, ItemStack itemStack, Consumer<ItemClick> consumer) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.itemStack = itemStack;
        this.consumer = consumer;
    }

    public Consumer<ItemClick> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<ItemClick> consumer) {
        this.consumer = consumer;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public @NotNull List<String> getLore(Player player) {
        if (getReplacements() == null) {
            return getLore();
        }

        Map<String, Object> replacements = getReplacements().apply(player);
        List<String> lore = new ArrayList<>();
        for (String line : getLore()) {
            for (Map.Entry<String, Object> entry : replacements.entrySet()) {
                line = line.replace(entry.getKey(), entry.getValue().toString());
            }
            lore.add(line);
        }

        return lore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(Player player) {
        if (getReplacements() == null) {
            return getName();
        }

        Map<String, Object> replacements = getReplacements().apply(player);
        String name = getName();
        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            name = name.replace(entry.getKey(), entry.getValue().toString());
        }

        return name;
    }

    public Function<Player, Map<String, Object>> getReplacements() {
        return replacements;
    }

    public void setReplacements(Function<Player, Map<String, Object>> replacements) {
        this.replacements = replacements;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public @NotNull GlobalItem copy() {
        return new GlobalItem(id, name, lore, slot, itemStack, replacements, consumer);
    }

    @Override
    public String toString() {
        return "GlobalItem{" +
                "consumer=" + consumer +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", slot=" + slot +
                ", itemStack=" + itemStack +
                ", replacements=" + replacements +
                '}';
    }
}
