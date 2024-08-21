package me.opkarol.oplibrary.injection.inventories.injection;

import me.opkarol.oplibrary.debug.PluginDebugger;
import me.opkarol.oplibrary.injection.DependencyInjection;
import me.opkarol.oplibrary.injection.Inject;
import me.opkarol.oplibrary.injection.formatter.DefaultTextFormatter;
import me.opkarol.oplibrary.injection.inventories.GlobalInventory;
import me.opkarol.oplibrary.injection.inventories.items.GlobalItem;
import me.opkarol.oplibrary.util.ClassFinder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InventoriesInjectionManager {
    @Inject
    private static InventoriesManager inventoriesManager;
    @Inject
    private static DefaultTextFormatter textFormatter;

    @Inject
    public InventoriesInjectionManager() {
        autoInject();
    }

    public static void autoInject() {
        Set<Class<?>> set = DependencyInjection.get(ClassFinder.class).findAllClassesUsingClassLoader();
        PluginDebugger debugger = DependencyInjection.get(PluginDebugger.class);

        for (Class<?> clazz : set) {
            debugger.debug("Found Class: " + clazz.getName());
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(GlobalInventory.class) && Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        String id = field.getName();
                        GlobalInventory inventory = (GlobalInventory) field.get(null);
                        String title = inventoriesManager.getTitle(id, textFormatter.formatTitle(inventory.getTitle()));
                        inventory.setTitle(title);
                        inventoriesManager.setTitle(id, title);
                        debugger.debug("Setting title of " + id + " to " + title);

                        List<GlobalItem> items = new ArrayList<>();
                        for (GlobalItem item : inventory.getItems()) {
                            String name = inventoriesManager.getItemName(id, item.getId(), textFormatter.formatItemName(item.getName()));
                            inventoriesManager.setItemName(id, item.getId(), name);
                            debugger.debug("Setting item name of " + item.getId() + " to " + name);

                            List<String> lore = inventoriesManager.getItemLore(id, item.getId(), textFormatter.formatItemLore(item.getLore()));
                            inventoriesManager.setItemLore(id, item.getId(), lore);
                            debugger.debug("Setting lore of " + item.getId() + " to " + lore);

                            int slot = inventoriesManager.getItemSlot(id, item.getId(), item.getSlot());
                            if (slot != -1) {
                                inventoriesManager.setItemSlot(id, item.getId(), slot);
                                debugger.debug("Setting slot of " + item.getId() + " to " + slot);
                            }

                            ItemStack itemStack = inventoriesManager.getItemStack(id, item.getId(), item.getItemStack());
                            inventoriesManager.setItemStack(id, item.getId(), itemStack);
                            debugger.debug("Setting item stack of " + item.getId() + " to " + itemStack);

                            items.add(new GlobalItem(id, name, lore, slot, itemStack, item.getReplacements(), item.getConsumer()));
                        }
                        inventory.setItems(items);

                        field.set(null, inventory);
                    } catch (IllegalAccessException ignored) {
                        debugger.debug("Failed to inject value into field: " + field.getName() + " of class: " + clazz.getName());
                    }
                } else if (field.getType().equals(GlobalItem.class) && Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        String id = "common";
                        GlobalItem item = (GlobalItem) field.get(null);
                        String name = inventoriesManager.getItemName(id, item.getId(), item.getName());
                        inventoriesManager.setItemName(id, item.getId(), name);
                        item.setName(name);
                        debugger.debug("Setting item name of " + item.getId() + " to " + name);

                        List<String> lore = inventoriesManager.getItemLore(id, item.getId(), item.getLore());
                        inventoriesManager.setItemLore(id, item.getId(), lore);
                        item.setLore(lore);
                        debugger.debug("Setting lore of " + item.getId() + " to " + lore);

                        ItemStack itemStack = inventoriesManager.getItemStack(id, item.getId(), item.getItemStack());
                        inventoriesManager.setItemStack(id, item.getId(), itemStack);
                        item.setItemStack(itemStack);
                        debugger.debug("Setting item stack of " + item.getId() + " to " + itemStack);

                        field.set(null, item);
                    } catch (IllegalAccessException ignored) {
                        debugger.debug("Failed to inject value into field: " + field.getName() + " of class: " + clazz.getName());
                    }
                }
            }
        }
    }
}