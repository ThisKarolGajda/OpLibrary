package me.opkarol.oplibrary.inventories;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.configurationfile.ConfigurationFile;
import me.opkarol.oplibrary.listeners.BasicListener;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractInventory extends BasicListener {
    private final String title;
    private final Map<String, ItemStackTranslatable> items = new HashMap<>();

    public AbstractInventory(String path) {
        ConfigurationFile file = Plugin.getInstance().getInventoriesFile();
        title = file.get(path + ".title").toString();

        file.getConfigurationSectionKeys(path + ".items").ifPresent(strings -> strings.forEach(string -> {
            String name = file.get(path + ".items." + string + ".name").toString();
            List<String> lore = file.getFileConfiguration().getStringList(path + ".items." + string + ".lore");
            items.put(string, new ItemStackTranslatable(FormatTool.formatMessage(name), FormatTool.formatList(lore)));
        }));

        file.getConfigurationSectionKeys("global").ifPresent(strings -> strings.forEach(string -> {
            String name = file.get("global." + string + ".name").toString();
            List<String> lore = file.getFileConfiguration().getStringList("global." + string + ".lore");
            items.put(string, new ItemStackTranslatable(FormatTool.formatMessage(name), FormatTool.formatList(lore)));
        }));
    }

    public String getTitle() {
        return FormatTool.formatMessage(title);
    }

    public Map<String, ItemStackTranslatable> getItems() {
        return items;
    }

    record ItemStackTranslatable(String name, List<String> lore) {

    }

    public record InteractableItem(ItemStack itemStack, Consumer<InventoryClickEvent> action) {

    }
}
