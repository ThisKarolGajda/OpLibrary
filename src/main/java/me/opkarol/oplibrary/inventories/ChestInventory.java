package me.opkarol.oplibrary.inventories;

import me.opkarol.oplibrary.tools.FormatTool;
import me.opkarol.oplibrary.tools.HeadManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class ChestInventory extends AbstractInventory {
    private static final InteractableItem EMPTY_ITEM = new InteractableItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k").secure(), (event) -> event.setCancelled(true));
    private final Map<Integer, Map<Integer, InteractableItem>> items = new HashMap<>();
    private final Map<Integer, Inventory> inventoriesCache = new HashMap<>();
    private final Map<Integer, InteractableItem> globalItems = new HashMap<>();
    private Inventory inventory;
    private final int rows;
    private int currentPage = 0;
    private Consumer<InventoryClickEvent> clickEventConsumer;
    private Consumer<InventoryDragEvent> dragEventConsumer;

    public ChestInventory(String path, int rows) {
        super(path);
        this.rows = rows;
    }

    public void fillEmpty(String id, int page, ItemStack item, Consumer<InventoryClickEvent> action) {
        fillEmpty(id, page, item, action, new HashMap<>());
    }

    public void fillEmpty(String id, ItemStack item, Consumer<InventoryClickEvent> action) {
        fillEmpty(id, currentPage, item, action, new HashMap<>());
    }

    public void fillEmpty(String id, int page, ItemStack item, Consumer<InventoryClickEvent> action, Map<String, String> replacements) {
        for (int slot = 0; slot < rows * 9; slot++) {
            if (globalItems.containsKey(slot)) {
                continue;
            }

            if (items.containsKey(page) && items.get(page).containsKey(slot)) {
                continue;
            }

            setItem(id, page, slot, item, action, replacements);
        }
    }

    public void setNextFree(String id, ItemStack item, Consumer<InventoryClickEvent> action, Map<String, String> replacements) {
        int pages = items.keySet().size() + 1;
        for (int page = 0; page < pages; page++) {
            for (int slot = 0; slot < rows * 9; slot++) {
                if (globalItems.containsKey(slot)) {
                    continue;
                }

                if (items.containsKey(page) && items.get(page).containsKey(slot)) {
                    continue;
                }

                setItem(id, page, slot, item, action, replacements);
                return;
            }
        }
    }

    public void setNextFree(String id, ItemStack item, Consumer<InventoryClickEvent> action) {
        setNextFree(id, item, action, new HashMap<>());
    }

    public void setItem(String id, int slot, ItemStack item, Consumer<InventoryClickEvent> action, Map<String, String> replacements) {
        setItem(id, 0, slot, item, action, replacements);
    }

    public void setItem(String id, int slot, Material material, Consumer<InventoryClickEvent> action, Map<String, String> replacements) {
        setItem(id, 0, slot, new ItemBuilder(material), action, replacements);
    }

    public void setItem(String id, int slot, Material material, Consumer<InventoryClickEvent> action) {
        setItem(id, 0, slot, new ItemBuilder(material), action, new HashMap<>());
    }

    public void setItem(String id, int slot, Material material, boolean glowing, Consumer<InventoryClickEvent> action) {
        setItem(id, 0, slot, glowing ? new ItemBuilder(material).setEnchants(Map.of(Enchantment.LUCK, 1)).setFlags(ItemFlag.HIDE_ENCHANTS) : new ItemBuilder(material), action, new HashMap<>());
    }

    public void setItem(String id, int slot, Material material, boolean glowing, Consumer<InventoryClickEvent> action, Map<String, String> replacements) {
        setItem(id, 0, slot, glowing ? new ItemBuilder(material).setEnchants(Map.of(Enchantment.LUCK, 1)).setFlags(ItemFlag.HIDE_ENCHANTS) : new ItemBuilder(material), action, replacements);
    }

    public void setItem(String id, int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        setItem(id, 0, slot, item, action, new HashMap<>());
    }

    public void setGlobalItem(String id, int index, ItemStack item, Consumer<InventoryClickEvent> action) {
        ItemStackTranslatable itemStackTranslatable = getItems().getOrDefault(id, null);
        if (itemStackTranslatable == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        String name = itemStackTranslatable.name();
        meta.setDisplayName(FormatTool.formatMessage(name));
        meta.setLore(FormatTool.formatList(itemStackTranslatable.lore()));
        item.setItemMeta(meta);

        setGlobalItem(index, new InteractableItem(new ItemBuilder(item).secure(), action));
    }

    public void setGlobalItem(String id, int index, ItemStack item, Consumer<InventoryClickEvent> action, Map<String, String> replacements) {
        ItemStackTranslatable itemStackTranslatable = getItems().getOrDefault(id, null);
        if (itemStackTranslatable == null) {
            return;
        }

        // Name
        ItemMeta meta = item.getItemMeta();
        AtomicReference<String> name = new AtomicReference<>(itemStackTranslatable.name());
        replacements.forEach((replace, replacement) -> name.set(name.get().replace(replace, replacement)));
        meta.setDisplayName(FormatTool.formatMessage(name.get()));

        // Lore
        AtomicReference<List<String>> lore = new AtomicReference<>(itemStackTranslatable.lore());
        replacements.forEach((replace, replacement) -> {
            List<String> newLore = new ArrayList<>();

            lore.get().forEach(string -> newLore.add(string.replace(replace, replacement)));

            lore.set(newLore);
        });
        meta.setLore(FormatTool.formatList(lore.get()));
        item.setItemMeta(meta);

        setGlobalItem(index, new InteractableItem(new ItemBuilder(item).secure(), action));
    }

    public void setGlobalItem(int index, InteractableItem item) {
        globalItems.put(index, item);
    }

    public void setItem(String id, int pageIndex, int slot, ItemStack item, Consumer<InventoryClickEvent> action, Map<String, String> replacements) {
        ItemStackTranslatable itemStackTranslatable = getItems().getOrDefault(id, null);
        if (itemStackTranslatable == null) {
            return;
        }

        // Name
        ItemMeta meta = item.getItemMeta();
        AtomicReference<String> name = new AtomicReference<>(itemStackTranslatable.name());
        replacements.forEach((replace, replacement) -> name.set(name.get().replace(replace, replacement)));
        meta.setDisplayName(FormatTool.formatMessage(name.get()));

        // Lore
        AtomicReference<List<String>> lore = new AtomicReference<>(itemStackTranslatable.lore());
        replacements.forEach((replace, replacement) -> {
            List<String> newLore = new ArrayList<>();

            lore.get().forEach(string -> newLore.add(string.replace(replace, replacement)));

            lore.set(newLore);
        });
        meta.setLore(FormatTool.formatList(lore.get()));
        item.setItemMeta(meta);

        Map<Integer, InteractableItem> page = getOrCreatePage(pageIndex);
        page.put(slot, new InteractableItem(new ItemBuilder(item).secure(), action));
        items.put(pageIndex, page);
    }

    public void setItem(int pageIndex, int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        Map<Integer, InteractableItem> page = getOrCreatePage(pageIndex);
        page.put(slot, new InteractableItem(new ItemBuilder(item).secure(), action));
        items.put(pageIndex, page);
    }

    public void setItem(int pageIndex, int slot, InteractableItem item) {
        Map<Integer, InteractableItem> page = getOrCreatePage(pageIndex);
        page.put(slot, new InteractableItem(new ItemBuilder(item.itemStack()).secure(), item.action()));
        items.put(pageIndex, page);
    }

    public void setItem(int slot, InteractableItem item) {
        this.setItem(0, slot, item);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        this.setItem(0, slot, item, action);
    }

    public void setItemNextPage(int slot, Player player) {
        setGlobalItem("next_page", slot, new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")), event -> {
            event.setCancelled(true);
            nextPage();
            open(player);
        });
    }

    public void setItemPreviousPage(int slot, Player player) {
        setGlobalItem("previous_page", slot, new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9")), event -> {
            event.setCancelled(true);
            previousPage();
            open(player);
        });
    }

    public void setItemHome(int slot, Player player, Runnable runnable) {
        setGlobalItem("home_page", slot, new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f")), event -> {
            event.setCancelled(true);
            runnable.run();
        });
    }

    public void setGlobalEmpty(int slot) {
        setGlobalItem(slot, EMPTY_ITEM);
    }

    public void setGlobalRowEmptyIfNotTaken(int row) {
        for (int i = (row - 1) * 9; i < row * 9; i++) {
            if (!globalItems.containsKey(i)) {
                setGlobalEmpty(i);
            }
        }
    }

    public void fillEmptyWithBlank() {
        fillEmptyWithBlank(0);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!hasPageAnyItems(i)) {
                break;
            }

            fillEmptyWithBlank(i);
        }
    }

    public void fillEmptyWithBlank(int page) {
        for (int slot = 0; slot < rows * 9; slot++) {
            if (globalItems.containsKey(slot)) {
                continue;
            }

            if (items.containsKey(page) && items.get(page).containsKey(slot)) {
                continue;
            }

            Map<Integer, InteractableItem> pageMap = getOrCreatePage(page);
            pageMap.put(slot, EMPTY_ITEM);
            items.put(page, pageMap);
        }
    }

    private Map<Integer, InteractableItem> getOrCreatePage(Integer page) {
        return items.containsKey(page) ? items.get(page) : new HashMap<>();
    }

    public void build(int page) {
        if (inventoriesCache.containsKey(page)) {
            inventory = inventoriesCache.get(page);
            return;
        }

        inventory = Bukkit.createInventory(new InventoryHolder(getItemsPage(page), clickEventConsumer, dragEventConsumer), rows * 9, getTitle());
        items.getOrDefault(page, new HashMap<>()).forEach((slot, action) -> inventory.setItem(slot, action.itemStack()));
        globalItems.forEach((slot, action) -> inventory.setItem(slot, action.itemStack()));
        inventoriesCache.put(page, inventory);
    }

    private @NotNull Map<Integer, InteractableItem> getItemsPage(int pageIndex) {
        Map<Integer, InteractableItem> page = new HashMap<>(getOrCreatePage(pageIndex));
        page.putAll(globalItems);
        return page;
    }

    private boolean hasPageAnyItems(int page) {
        return items.containsKey(page) && !items.get(page).isEmpty();
    }

    public void nextPage() {
        if (!hasPageAnyItems(currentPage + 1)) {
            return;
        }

        currentPage++;
    }

    public void previousPage() {
        if (currentPage == 0) {
            return;
        }

        currentPage--;
    }

    public void build() {
        build(currentPage);
    }

    public void open(@NotNull Player player) {
        build();
        player.openInventory(inventory);
    }

    public void open(@NotNull Player player, int page) {
        build(page);
        player.openInventory(inventory);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setClickEventConsumer(Consumer<InventoryClickEvent> clickEventConsumer) {
        this.clickEventConsumer = clickEventConsumer;
    }

    public void setDragEventConsumer(Consumer<InventoryDragEvent> dragEventConsumer) {
        this.dragEventConsumer = dragEventConsumer;
    }

    public record InventoryHolder(
            Map<Integer, InteractableItem> slotsActions, @Nullable Consumer<InventoryClickEvent> clickEventConsumer, @Nullable Consumer<InventoryDragEvent> dragEventConsumer) implements org.bukkit.inventory.InventoryHolder {

        @NotNull
        @Override
        public org.bukkit.inventory.Inventory getInventory() {
            return null;
        }
    }
}