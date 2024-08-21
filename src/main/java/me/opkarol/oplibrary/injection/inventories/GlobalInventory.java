package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.inventories.items.GlobalItem;
import me.opkarol.oplibrary.injection.inventories.items.ItemClick;
import me.opkarol.oplibrary.inventories.ItemBuilder;
import me.opkarol.oplibrary.tools.FormatTool;
import me.opkarol.oplibrary.tools.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GlobalInventory implements Cloneable {
    private final int rows;
    private String title;
    private List<GlobalItem> items = new LinkedList<>();

    private FillShape fillShape;
    private FillType fillType;
    private ItemBuilder filler;

    private int currentPage = 1;
    private boolean hasList = false;

    private Consumer<Player> homePageAction;

    private Consumer<InventoryClickEvent> clickEventConsumer;
    private Consumer<InventoryDragEvent> dragEventConsumer;

    public GlobalInventory(String title, int rows, List<GlobalItem> items, Consumer<Player> homePageAction, boolean hasList, FillType fillType, FillShape fillShape, ItemBuilder filler, Consumer<InventoryDragEvent> dragEventConsumer, int currentPage, Consumer<InventoryClickEvent> clickEventConsumer) {
        this.title = title;
        this.rows = rows;
        this.items = items;
        this.homePageAction = homePageAction;
        this.hasList = hasList;
        this.fillType = fillType;
        this.fillShape = fillShape;
        this.filler = filler;
        this.dragEventConsumer = dragEventConsumer;
        this.currentPage = currentPage;
        this.clickEventConsumer = clickEventConsumer;
    }

    private static final GlobalItem PREVIOUS_PAGE = new GlobalItem("previous-page", "Poprzednia strona", List.of(), -1, Heads.get("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9"), click -> {
    });
    private static final GlobalItem NEXT_PAGE = new GlobalItem("next-page", "Następna strona", List.of(), -1, Heads.get("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"), click -> {
    });
    private static final GlobalItem HOME_PAGE = new GlobalItem("home-page", "Wróć", List.of(), -1, Heads.get("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"), click -> {
    });

    public GlobalInventory(String title, int rows) {
        this.title = FormatTool.formatMessage(title);
        this.rows = rows;
    }

    public static @NotNull GlobalInventory create(String title, int rows) {
        return new GlobalInventory(title, rows);
    }

    public static @NotNull GlobalInventory row1(String title) {
        return new GlobalInventory(title, 1);
    }

    public static @NotNull GlobalInventory row2(String title) {
        return new GlobalInventory(title, 2);
    }

    public static @NotNull GlobalInventory row3(String title) {
        return new GlobalInventory(title, 3);
    }

    public static @NotNull GlobalInventory row4(String title) {
        return new GlobalInventory(title, 4);
    }

    public static @NotNull GlobalInventory row5(String title) {
        return new GlobalInventory(title, 5);
    }

    public static @NotNull GlobalInventory row6(String title) {
        return new GlobalInventory(title, 6);
    }

    public List<GlobalItem> getItems() {
        return items;
    }

    public void setItems(List<GlobalItem> items) {
        this.items = items;
    }

    public GlobalInventory item(String id, String name, List<String> lore, int slot, ItemStack itemStack, Consumer<ItemClick> consumer) {
        items.add(new GlobalItem(id, name, lore, slot, itemStack, consumer));
        return this;
    }

    public GlobalInventory item(String name, List<String> lore, int slot, ItemStack itemStack, Consumer<ItemClick> consumer) {
        return item(getIdFromName(name), name, lore, slot, itemStack, consumer);
    }

    public GlobalInventory item(String id, String name, List<String> lore, int slot, ItemStack itemStack, Function<Player, Map<String, Object>> replacements, Consumer<ItemClick> consumer) {
        items.add(new GlobalItem(id, name, lore, slot, itemStack, replacements, consumer));
        return this;
    }

    public GlobalInventory item(String name, List<String> lore, int slot, ItemStack itemStack, Function<Player, Map<String, Object>> replacements, Consumer<ItemClick> consumer) {
        return item(getIdFromName(name), name, lore, slot, itemStack, replacements, consumer);
    }

    public String getIdFromName(@NotNull String name) {
        String connected = String.join("-", name.split(" ")).toLowerCase();
        if (!hasItem(connected)) {
            return connected;
        }

        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (hasItem(uuid.toString()));

        return uuid.toString();
    }

    public boolean hasItem(String id) {
        return items.stream().anyMatch(item -> item.getId().equals(id));
    }

    public GlobalInventory fill(FillShape shape, FillType type, @Nullable ItemBuilder filler) {
        this.fillShape = shape;
        this.fillType = type;
        this.filler = filler;
        return this;
    }

    public GlobalInventory fill(FillShape shape, FillType type) {
        return fill(shape, type, null);
    }

    public GlobalInventory fillEmpty(FillShape shape) {
        return fill(shape, FillType.EMPTY);
    }

    public GlobalInventory fillAllEmpty() {
        return fill(FillShape.ALL, FillType.EMPTY);
    }

    public String getTitle() {
        return FormatTool.formatMessage(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRows() {
        return rows;
    }

    public GlobalInventory setHomePageAction(Consumer<Player> action) {
        this.homePageAction = action;
        return this;
    }

    public void open(Player player) {
        List<GlobalItem> displayedItems = new ArrayList<>();
        Inventory inventory = Bukkit.createInventory(new GlobalInventoryHandler(displayedItems, clickEventConsumer, dragEventConsumer), rows * 9, getTitle());

        ItemBuilder filler = (this.filler != null) ? this.filler : new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k");
        fillInventoryBorders(inventory, filler);

        int startIndex = (currentPage - 1) * getItemsPerPage();
        int endIndex = Math.min(startIndex + getItemsPerPage(), getUnsetItems().size());
        List<GlobalItem> globalItems = getUnsetItems().subList(startIndex, endIndex);

        System.out.println("Start index: " + startIndex);
        System.out.println("End index: " + endIndex);
        System.out.println("Current page: " + currentPage);

        for (GlobalItem item : globalItems) {
            item = item.clone();
            int slot = findAvailableSlot(inventory);
            if (slot != -1) {
                item.setSlot(slot);
                displayedItems.add(item);
                ItemBuilder builder = new ItemBuilder(item.getItemStack()).setName(item.getName(player)).setLore(item.getLore(player));
                inventory.setItem(slot, builder);
            }
        }

        addPaginationControls(player, inventory, displayedItems);

        for (GlobalItem item : getSetItems()) {
            item = item.clone();
            setItem(player, item, inventory, displayedItems);
        }

        ((GlobalInventoryHandler) inventory.getHolder()).setItems(displayedItems);

        if (fillShape != null) {
            if (filler == null) {
                filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k");
            }

            switch (fillShape) {
                case ALL:
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (shouldFillSlot(FillType.EMPTY, inventory, i)) {
                            inventory.setItem(i, filler.generate());
                        }
                    }
                    break;

                case BORDER:
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < 9; j++) {
                            int slot = i * 9 + j;
                            if ((i == 0 || i == rows - 1 || j == 0 || j == 8) && shouldFillSlot(FillType.EMPTY, inventory, slot)) {
                                inventory.setItem(slot, filler.generate());
                            }
                        }
                    }
                    break;

                case CROSS:
                    for (int i = 0; i < rows; i++) {
                        int verticalSlot = (i * 9) + (rows / 2);
                        int horizontalSlot = (rows / 2 * 9) + i;
                        if (shouldFillSlot(FillType.EMPTY, inventory, verticalSlot)) {
                            inventory.setItem(verticalSlot, filler.generate());
                        }
                        if (shouldFillSlot(FillType.EMPTY, inventory, horizontalSlot)) {
                            inventory.setItem(horizontalSlot, filler.generate());
                        }
                    }
                    break;

                case CENTER:
                    int centerSlot = (rows / 2 * 9) + (rows / 2);
                    if (shouldFillSlot(FillType.EMPTY, inventory, centerSlot)) {
                        inventory.setItem(centerSlot, filler.generate());
                    }
                    break;

                case DIAGONAL:
                    for (int i = 0; i < rows; i++) {
                        int diagonalSlot = i * 9 + i;
                        if (shouldFillSlot(FillType.EMPTY, inventory, diagonalSlot)) {
                            inventory.setItem(diagonalSlot, filler.generate());
                        }
                    }
                    break;
            }
        }

        player.openInventory(inventory);
    }

    private static void setItem(Player player, GlobalItem item, Inventory inventory, List<GlobalItem> displayedItems) {
        ItemBuilder builder = new ItemBuilder(item.getItemStack()).setName(item.getName(player)).setLore(item.getLore(player));
        inventory.setItem(item.getSlot(), builder);
        displayedItems.add(item);
    }

    private void fillInventoryBorders(Inventory inventory, ItemBuilder filler) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 9; j++) {
                if (i == 0 || i == rows - 1 || j == 0 || j == 8) {
                    inventory.setItem(i * 9 + j, filler.generate());
                }
            }
        }
    }

    private List<GlobalItem> getUnsetItems() {
        return getItems().stream()
                .filter(item -> item.getSlot() == -1)
                .collect(Collectors.toList());
    }

    private List<GlobalItem> getSetItems() {
        return getItems().stream()
                .filter(item -> item.getSlot() != -1)
                .collect(Collectors.toList());
    }

    private int findAvailableSlot(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }

    private boolean shouldFillSlot(@NotNull FillType fillType, Inventory inventory, int slot) {
        return switch (fillType) {
            case EMPTY -> inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR;
            case SOLID -> true;
            case CUSTOM -> true;
        };
    }

    private void addPaginationControls(Player player, @NotNull Inventory inventory, List<GlobalItem> displayedItems) {
        if (homePageAction != null) {
            GlobalItem home = HOME_PAGE.clone();
            home.setSlot(inventory.getSize() - 5);
            home.setConsumer(click -> {
                click.cancel();
                homePageAction.accept(click.getPlayer());
            });
            setItem(player, home, inventory, displayedItems);

            GlobalItem previous = PREVIOUS_PAGE.clone();
            previous.setSlot(inventory.getSize() - 6);
            previous.setConsumer(click -> {
                click.cancel();
                if (currentPage > 1) {
                    setCurrentPage(currentPage - 1);
                    open(click.getPlayer());
                }
            });
            setItem(player, previous, inventory, displayedItems);

            GlobalItem next = NEXT_PAGE.clone();
            next.setSlot(inventory.getSize() - 4);
            next.setConsumer(click -> {
                click.cancel();
                if (!((currentPage) * getItemsPerPage() >= getUnsetItems().size())) {
                    setCurrentPage(currentPage + 1);
                    open(click.getPlayer());
                }
            });
            setItem(player, next, inventory, displayedItems);

        } else {
            GlobalItem previous = PREVIOUS_PAGE.clone();
            previous.setSlot(inventory.getSize() - 6);
            previous.setConsumer(click -> {
                click.cancel();
                if (currentPage > 1) {
                    setCurrentPage(currentPage - 1);
                    open(click.getPlayer());
                }
            });
            setItem(player, previous, inventory, displayedItems);

            GlobalItem next = NEXT_PAGE.clone();
            next.setSlot(inventory.getSize() - 4);
            next.setConsumer(click -> {
                click.cancel();
                if (!((currentPage) * getItemsPerPage() >= getUnsetItems().size())) {
                    setCurrentPage(currentPage + 1);
                    open(click.getPlayer());
                }
            });
            setItem(player, next, inventory, displayedItems);
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = Math.max(1, currentPage);
    }

    public int getItemsPerPage() {
        return (rows - 2) * 7;
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, ItemStack itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull List<K> list, BiConsumer<K, ItemClick> onClick) {
        hasList = true;
        GlobalItem item = new GlobalItem(id, name, lore, -1, itemStack, (click) -> {
        });
        for (K k : list) {
            GlobalItem clone = item.clone();
            clone.setConsumer(click -> onClick.accept(k, click));
            clone.setReplacements(player -> replacements.apply(k, player));
            clone.setSlot(-1);
            items.add(clone);
        }

        return this;
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, ItemStack itemStack, @NotNull List<K> list, BiConsumer<K, ItemClick> onClick) {
        hasList = true;
        GlobalItem item = new GlobalItem(id, name, lore, -1, itemStack, (click) -> {
        });
        for (K k : list) {
            GlobalItem clone = item.clone();
            clone.setConsumer(click -> onClick.accept(k, click));
            clone.setSlot(-1);
            items.add(clone);
        }

        return this;
    }

    public <K> GlobalInventory list(String name, List<String> lore, ItemStack itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull List<K> list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, replacements, list, onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, ItemStack itemStack, @NotNull List<K> list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, list, onClick);
    }

    public Consumer<InventoryDragEvent> getDragEventConsumer() {
        return dragEventConsumer;
    }

    public void setDragEventConsumer(Consumer<InventoryDragEvent> dragEventConsumer) {
        this.dragEventConsumer = dragEventConsumer;
    }

    public Consumer<InventoryClickEvent> getClickEventConsumer() {
        return clickEventConsumer;
    }

    public void setClickEventConsumer(Consumer<InventoryClickEvent> clickEventConsumer) {
        this.clickEventConsumer = clickEventConsumer;
    }

    public enum FillType {
        EMPTY,
        SOLID,
        CUSTOM,
    }

    public enum FillShape {
        ALL,
        BORDER,
        CROSS,
        CENTER,
        DIAGONAL,
    }

    @Override
    public GlobalInventory clone() {
        return new GlobalInventory(title, rows, items, homePageAction, hasList, fillType, fillShape, filler, dragEventConsumer, currentPage, clickEventConsumer);
    }
}