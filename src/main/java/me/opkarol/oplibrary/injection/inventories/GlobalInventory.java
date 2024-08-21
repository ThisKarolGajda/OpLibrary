package me.opkarol.oplibrary.injection.inventories;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.injection.inventories.items.GlobalItem;
import me.opkarol.oplibrary.injection.inventories.items.ItemClick;
import me.opkarol.oplibrary.inventories.ItemBuilder;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@IgnoreInject
public class GlobalInventory {
    private String title;
    private final int rows;
    private List<GlobalItem> items = new ArrayList<>();

    private FillShape fillShape;
    private FillType fillType;
    private ItemBuilder filler;

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


    public GlobalInventory add(String id, String name, List<String> lore, int slot, ItemBuilder itemBuilder, Consumer<ItemClick> consumer) {
        items.add(new GlobalItem(id, name, lore, slot, itemBuilder, consumer));
        return this;
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
        return title;
    }

    public int getRows() {
        return rows;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new GlobalInventoryHandler(items, event -> {
        }, event -> {
        }), rows * 9, getTitle());

        for (GlobalItem item : items) {
            inventory.setItem(item.slot(), new ItemBuilder(item.itemStack()).setName(item.name()).setLore(item.lore()));
        }

        if (fillShape != null) {
            ItemBuilder filler = this.filler;
            if (filler == null) {
                filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k"); // Default filler
            }

            switch (fillShape) {
                case ALL:
                    for (int i = 0; i < inventory.getSize(); i++) {
                        inventory.setItem(i, filler.generate());
                    }
                    break;

                case BORDER:
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < 9; j++) {
                            if (i == 0 || i == rows - 1 || j == 0 || j == 8) {
                                inventory.setItem(i * 9 + j, filler.generate());
                            }
                        }
                    }
                    break;

                case CROSS:
                    for (int i = 0; i < rows; i++) {
                        inventory.setItem((i * 9) + (rows / 2), filler.generate());
                        inventory.setItem((rows / 2 * 9) + i, filler.generate());
                    }
                    break;

                case CENTER:
                    inventory.setItem((rows / 2 * 9) + (rows / 2), filler.generate());
                    break;

                case DIAGONAL:
                    for (int i = 0; i < rows; i++) {
                        inventory.setItem(i * 9 + i, filler.generate());
                    }
                    break;
            }
        }

        player.openInventory(inventory);
    }

    public void setItems(List<GlobalItem> items) {
        this.items = items;
    }

    public enum FillType {
        EMPTY,
        ALL,
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

    public void setTitle(String title) {
        this.title = title;
    }
}
