package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.formatter.LoreBuilder;
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

@SuppressWarnings("unused")
public class GlobalInventory {
    private final int rows;
    private String title;
    private List<GlobalItem> items = new LinkedList<>();
    private FillShape fillShape;
    private FillType fillType;
    private ItemBuilder filler;
    private int currentPage = 1;
    private Consumer<Player> homePageAction;
    private Consumer<InventoryClickEvent> clickEventConsumer;
    private Consumer<InventoryDragEvent> dragEventConsumer;
    private Consumer<GlobalInventory> inventoryGlobalBuilder;
    private BiConsumer<GlobalInventory, Player> inventoryGlobalBuilderPlayer;
    private BiConsumer<GlobalInventory, ArgsMap> inventoryGlobalBuilderArgs;
    private Map<String, Object> args;

    public GlobalInventory(String title, int rows, List<GlobalItem> items, Consumer<Player> homePageAction, boolean hasList, FillType fillType, FillShape fillShape, ItemBuilder filler, Consumer<InventoryDragEvent> dragEventConsumer, int currentPage, Consumer<InventoryClickEvent> clickEventConsumer, Map<String, Object> args) {
        this.title = title;
        this.rows = rows;
        this.items = items;
        this.homePageAction = homePageAction;
        this.fillType = fillType;
        this.fillShape = fillShape;
        this.filler = filler;
        this.dragEventConsumer = dragEventConsumer;
        this.currentPage = currentPage;
        this.clickEventConsumer = clickEventConsumer;
        this.args = args;
    }

    public static GlobalItem PREVIOUS_PAGE = new GlobalItem("previous-page", "&k", LoreBuilder.create().anyMouseButtonText("przejść z powrotem").build(), -1, Heads.get("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9"), click -> {
    });
    public static GlobalItem NEXT_PAGE = new GlobalItem("next-page", "&k", LoreBuilder.create().anyMouseButtonText("przejść dalej").build(), -1, Heads.get("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"), click -> {
    });
    public static GlobalItem HOME_PAGE = new GlobalItem("home-page", "&k", LoreBuilder.create().anyMouseButtonText("wrócić").build(), -1, Heads.get("5a6787ba32564e7c2f3a0ce64498ecbb23b89845e5a66b5cec7736f729ed37"), click -> {
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
        return addItem(new GlobalItem(id, name, lore, slot, itemStack, consumer));
    }

    public GlobalInventory item(GlobalItem @NotNull ... globalItems) {
        for (GlobalItem item : globalItems) {
            addItem(item);
        }
        return this;
    }

    public GlobalInventory item(String name, List<String> lore, int slot, ItemStack itemStack, Consumer<ItemClick> consumer) {
        return item(getIdFromName(name), name, lore, slot, itemStack, consumer);
    }

    public GlobalInventory item(String id, String name, List<String> lore, int slot, ItemStack itemStack, Function<Player, Map<String, Object>> replacements, Consumer<ItemClick> consumer) {
        return addItem(new GlobalItem(id, name, lore, slot, itemStack, replacements, consumer));
    }

    public GlobalInventory item(String name, List<String> lore, int slot, ItemStack itemStack, Function<Player, Map<String, Object>> replacements, Consumer<ItemClick> consumer) {
        return item(getIdFromName(name), name, lore, slot, itemStack, replacements, consumer);
    }

    private GlobalInventory addItem(GlobalItem item) {
        class ItemHolder {
            String name;
            List<String> lore;
        }

        ItemHolder holder = new ItemHolder();
        items.removeIf(item1 -> {
            boolean shouldRemove = item1.getId().equals(item.getId()) && item1.getSlot() == item.getSlot();
            if (shouldRemove) {
                holder.name = item1.getName();
                holder.lore = item1.getLore();
            }
            return shouldRemove;
        });

        if (holder.name != null) {
            item.setName(holder.name);
            item.setLore(holder.lore);
        }

        items.add(item);
        return this;
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
        open(player, args);
    }

    public void open(Player player, Map<String, Object> args) {
        this.args = args;
        List<GlobalItem> displayedItems = new ArrayList<>();
        Inventory inventory = Bukkit.createInventory(new GlobalInventoryHandler(this, clickEventConsumer, dragEventConsumer), rows * 9, getTitle());

        if (inventoryGlobalBuilderPlayer != null) {
            inventoryGlobalBuilderPlayer.accept(this, player);
        }

        if (inventoryGlobalBuilder != null) {
            inventoryGlobalBuilder.accept(this);
        }

        if (inventoryGlobalBuilderArgs != null) {
            inventoryGlobalBuilderArgs.accept(this, new ArgsMap(args));
        }

        if (!getUnsetItems().isEmpty()) {
            ItemBuilder filler = (this.filler != null) ? this.filler : new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k");
            fillInventoryBorders(inventory, filler);

            int startIndex = (currentPage - 1) * getItemsPerPage();
            int endIndex = Math.min(startIndex + getItemsPerPage(), getUnsetItems().size());
            List<GlobalItem> globalItems = getUnsetItems().subList(startIndex, endIndex);

            for (GlobalItem item : globalItems) {
                item = item.copy();
                int slot = findAvailableSlot(inventory);
                if (slot != -1) {
                    item.setSlot(slot);
                    displayedItems.add(item);
                    ItemBuilder builder = new ItemBuilder(item.getItemStack()).setName(item.getName(player)).setLore(item.getLore(player));
                    inventory.setItem(slot, builder);
                }
            }

            addPaginationControls(player, inventory, displayedItems);
        }

        for (GlobalItem item : getSetItems()) {
            item = item.copy();
            setItem(player, item, inventory, displayedItems);
        }

        GlobalInventoryHandler handler = (GlobalInventoryHandler) inventory.getHolder();
        if (handler != null) {
            handler.setDisplayedItems(displayedItems);
        }

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

    private boolean shouldFillSlot(@NotNull FillType fillType, @NotNull Inventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);
        return switch (fillType) {
            case EMPTY -> item == null || item.getType() == Material.AIR;
            case SOLID -> true;
        };
    }

    private void addPaginationControls(Player player, @NotNull Inventory inventory, List<GlobalItem> displayedItems) {
        if (homePageAction != null) {
            GlobalItem home = HOME_PAGE.copy();
            home.setSlot(inventory.getSize() - 5);
            home.setConsumer(click -> {
                click.cancel();
                homePageAction.accept(click.getPlayer());
            });
            setItem(player, home, inventory, displayedItems);
            addBasicPaginationControl(player, inventory, displayedItems);
        } else {
            addBasicPaginationControl(player, inventory, displayedItems);
        }
    }

    private void addBasicPaginationControl(Player player, @NotNull Inventory inventory, List<GlobalItem> displayedItems) {
        GlobalItem previous = PREVIOUS_PAGE.copy();
        previous.setSlot(inventory.getSize() - 6);
        previous.setConsumer(click -> {
            click.cancel();
            if (currentPage > 1) {
                setCurrentPage(currentPage - 1);
                open(click.getPlayer());
            }
        });
        setItem(player, previous, inventory, displayedItems);

        GlobalItem next = NEXT_PAGE.copy();
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = Math.max(1, currentPage);
    }

    public int getItemsPerPage() {
        return (rows - 2) * 7;
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, ItemStack itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, replacements, list, onClick);
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, ItemStack itemStack, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, null, list, onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, ItemStack itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, replacements, list, onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, ItemStack itemStack, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, list, onClick);
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, ItemStack itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, replacements, List.of(list), onClick);
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, ItemStack itemStack, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, null, List.of(list), onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, ItemStack itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, replacements, list, onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, ItemStack itemStack, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, list, onClick);
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, Function<K, ItemStack> itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, replacements, list, onClick);
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, Function<K, ItemStack> itemStack, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, null, list, onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, Function<K, ItemStack> itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, replacements, list, onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, Function<K, ItemStack> itemStack, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, list, onClick);
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, Function<K, ItemStack> itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, replacements, List.of(list), onClick);
    }

    public <K> GlobalInventory list(String id, String name, List<String> lore, Function<K, ItemStack> itemStack, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return addItems(id, name, lore, itemStack, null, List.of(list), onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, Function<K, ItemStack> itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, replacements, list, onClick);
    }

    public <K> GlobalInventory list(String name, List<String> lore, Function<K, ItemStack> itemStack, @NotNull K[] list, BiConsumer<K, ItemClick> onClick) {
        return list(getIdFromName(name), name, lore, itemStack, list, onClick);
    }

    private <K> GlobalInventory addItems(String id, String name, List<String> lore, ItemStack itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        GlobalItem item = new GlobalItem(id, name, lore, -1, itemStack, (click) -> {
        });

        class ItemHolder {
            String name;
            List<String> lore;
        }

        ItemHolder holder = new ItemHolder();

        items.removeIf(item1 -> {
            boolean shouldRemove = item1.getSlot() == -1 && item1.getId().equals(id);
            if (shouldRemove) {
                holder.name = item1.getName();
                holder.lore = item1.getLore();
            }
            return shouldRemove;
        });

        if (holder.name != null) {
            item.setName(holder.name);
            item.setLore(holder.lore);
        }

        for (K k : list) {
            GlobalItem clone = item.copy();
            clone.setConsumer(click -> onClick.accept(k, click));
            if (replacements != null) {
                clone.setReplacements(player -> replacements.apply(k, player));
            }
            clone.setSlot(-1);
            items.add(clone);
        }

        return this;
    }

    private <K> GlobalInventory addItems(String id, String name, List<String> lore, Function<K, ItemStack> itemStack, BiFunction<K, Player, Map<String, Object>> replacements, @NotNull Iterable<K> list, BiConsumer<K, ItemClick> onClick) {
        GlobalItem item = new GlobalItem(id, name, lore, -1, null, (click) -> {
        });

        class ItemHolder {
            String name;
            List<String> lore;
        }

        ItemHolder holder = new ItemHolder();

        items.removeIf(item1 -> {
            boolean shouldRemove = item1.getSlot() == -1 && item1.getId().equals(id);
            if (shouldRemove) {
                holder.name = item1.getName();
                holder.lore = item1.getLore();
            }
            return shouldRemove;
        });

        if (holder.name != null) {
            item.setName(holder.name);
            item.setLore(holder.lore);
        }

        for (K k : list) {
            GlobalItem clone = item.copy();
            clone.setConsumer(click -> onClick.accept(k, click));
            if (replacements != null) {
                clone.setReplacements(player -> replacements.apply(k, player));
            }
            clone.setSlot(-1);
            clone.setItemStack(itemStack.apply(k));
            items.add(clone);
        }

        return this;
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

    public GlobalInventory builder(@NotNull Consumer<GlobalInventory> inventoryGlobalBuilder) {
        inventoryGlobalBuilder.accept(this);
        this.inventoryGlobalBuilder = inventoryGlobalBuilder;
        return this;
    }

    public GlobalInventory args(@NotNull BiConsumer<GlobalInventory, ArgsMap> inventoryGlobalBuilderArgs) {
        inventoryGlobalBuilderArgs.accept(this, new ArgsMap(new HashMap<>()));
        this.inventoryGlobalBuilderArgs = inventoryGlobalBuilderArgs;
        return this;
    }

    public enum FillType {
        EMPTY,
        SOLID,
    }

    public enum FillShape {
        ALL,
        BORDER,
        CROSS,
        CENTER,
        DIAGONAL,
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public <K> K getArg(String key) {
        return (K) args.get(key);
    }

    public Consumer<Player> getHomePageAction() {
        return homePageAction;
    }

    public FillShape getFillShape() {
        return fillShape;
    }

    public FillType getFillType() {
        return fillType;
    }

    public ItemBuilder getFiller() {
        return filler;
    }
}
