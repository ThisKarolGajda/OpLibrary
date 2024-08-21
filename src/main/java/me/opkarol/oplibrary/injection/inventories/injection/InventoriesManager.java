package me.opkarol.oplibrary.injection.inventories.injection;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.injection.file.FileManager;
import me.opkarol.oplibrary.inventories.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InventoriesManager {
    private final FileManager fileManager;

    public InventoriesManager(Plugin plugin) {
        this.fileManager = new FileManager(plugin, "inventories.yml");
    }

    public @NotNull String getItemName(String inventory, String key, String defaultName) {
        return fileManager.getValue(inventory + "." + key + ".name", defaultName);
    }

    public @NotNull List<String> getItemLore(String inventory, String key, List<String> defaultLore) {
        return fileManager.getValue(inventory + "." + key + ".lore", defaultLore);
    }

    public String getTitle(String inventory, String defaultTitle) {
        return fileManager.getValue(inventory + ".title", defaultTitle);
    }

    public int getItemSlot(String inventory, String key, int defaultSlot) {
        return fileManager.getValue(inventory + "." + key + ".slot", defaultSlot);
    }

    public ItemStack getItemStack(String inventory, String key, ItemStack itemStack) {
        return fileManager.getValue(inventory + "." + key + ".item", itemStack);
    }

    public void setItemName(String inventory, String key, String value) {
        fileManager.setValue(inventory + "." + key + ".name", value);
    }

    public void setItemLore(String inventory, String key, List<String> value) {
        fileManager.setValue(inventory + "." + key + ".lore", value);
    }

    public void setTitle(String inventory, String value) {
        fileManager.setValue(inventory + ".title", value);
    }

    public void setItemSlot(String inventory, String key, int value) {
        fileManager.setValue(inventory + "." + key + ".slot", value);
    }

    public void setItemStack(String inventory, String key, ItemStack itemStack) {
        if (itemStack instanceof ItemBuilder) {
            itemStack = ((ItemBuilder) itemStack).generate();
        }

        fileManager.setValue(inventory + "." + key + ".item", itemStack);
    }
}
