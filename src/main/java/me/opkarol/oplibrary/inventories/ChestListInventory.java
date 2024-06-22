package me.opkarol.oplibrary.inventories;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class ChestListInventory<I> {
    private final Player player;
    private final int rows;
    private final ChestInventory inventory;

    public ChestListInventory(Player player, String path, int rows) {
        this.player = player;
        this.rows = rows;

        if (rows <= 2) {
            throw new IllegalArgumentException("ChestListInventory must have at least 3 rows");
        }

        inventory = new ChestInventory(path, rows){};
        build();
        inventory.open(player);
    }

    public void build() {
        if (getOnHomeAction() != null) {
            inventory.setItemPreviousPage(rows * 9 - 3, player);
            inventory.setItemHome(rows * 9 - 2, player, getOnHomeAction());
            inventory.setItemNextPage(rows * 9 - 1, player);
        } else {
            inventory.setItemPreviousPage(rows * 9 - 2, player);
            inventory.setItemNextPage(rows * 9 - 1, player);
        }

        for (int row = 1; row < rows - 1; row++) {
            inventory.setGlobalBlank(row * 9);
            inventory.setGlobalBlank(row * 9 + 8);
        }

        inventory.setGlobalRowEmptyIfNotTaken(1);
        inventory.setGlobalRowEmptyIfNotTaken(rows);

        for (I i : getList()) {
            inventory.setNextFree(getItemId(), getItem().apply(i), event -> {
                event.setCancelled(true);
                onClick().accept(i);
            }, getReplacements().apply(i));
        }
    }

    public @Nullable Runnable getOnHomeAction() {
        return null;
    }

    public abstract String getItemId();

    public Function<I, Map<String, String>> getReplacements() {
        return (i) -> Map.of();
    }

    public abstract Function<I, ItemStack> getItem();

    public abstract Consumer<I> onClick();

    public abstract Iterable<? extends I> getList();

    public Player getPlayer() {
        return player;
    }

    @Deprecated(since = "2.0", forRemoval = true)
    public static class ChestListInventoryFactory {
        public static <I> @NotNull ChestListInventory<I> create(Player player, String path, int rows, String itemId, Function<I, ItemStack> item, Function<I, Map<String, String>> replacements, Consumer<I> onClick, List<? extends I> list) {
            return new ChestListInventoryBuilder<I>(path, itemId)
                    .withRows(rows)
                    .withItem(item)
                    .withReplacements(replacements)
                    .withOnClick(onClick)
                    .withList(list)
                    .build(player);
        }
    }

    public static class ChestListInventoryBuilder<I> {
        private String path;
        private int rows;
        private String itemId;
        private Function<I, ItemStack> item;
        private Function<I, Map<String, String>> replacements;
        private Consumer<I> onClick;
        private List<? extends I> list;
        private @Nullable Runnable onHomeAction;

        public ChestListInventoryBuilder(String path, String itemId) {
            this.path = path;
            this.itemId = itemId;
        }

        @Deprecated(since = "2.0", forRemoval = true)
        public ChestListInventoryBuilder<I> withPath(String path) {
            this.path = path;
            return this;
        }

        public ChestListInventoryBuilder<I> withOnHomeAction(Runnable onHomeAction) {
            this.onHomeAction = onHomeAction;
            return this;
        }

        public ChestListInventoryBuilder<I> withRows(int rows) {
            this.rows = rows;
            return this;
        }

        @Deprecated(since = "2.0", forRemoval = true)
        public ChestListInventoryBuilder<I> withItemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public ChestListInventoryBuilder<I> withItem(Function<I, ItemStack> item) {
            this.item = item;
            return this;
        }

        public ChestListInventoryBuilder<I> withReplacements(Function<I, Map<String, String>> replacements) {
            this.replacements = replacements;
            return this;
        }

        public ChestListInventoryBuilder<I> withOnClick(Consumer<I> onClick) {
            this.onClick = onClick;
            return this;
        }

        public ChestListInventoryBuilder<I> withList(List<? extends I> list) {
            this.list = list;
            return this;
        }

        @SafeVarargs
        public final ChestListInventoryBuilder<I> withList(I... object) {
            this.list = Arrays.stream(object).toList();
            return this;
        }

        public ChestListInventory<I> build(Player player) {
            return new ChestListInventory<>(player, path == null ? "NOT_PROVIDED" : path, rows == 0 ? list.size() > 20 ? 5 : list.size() > 10 ? 4 : 3 : rows) {
                @Override
                public String getItemId() {
                    return itemId;
                }

                @Override
                public Function<I, ItemStack> getItem() {
                    return item;
                }

                @Override
                public Function<I, Map<String, String>> getReplacements() {
                    return replacements;
                }

                @Override
                public Consumer<I> onClick() {
                    return onClick;
                }

                @Override
                public List<? extends I> getList() {
                    return list;
                }

                @Override
                public @Nullable Runnable getOnHomeAction() {
                    return onHomeAction;
                }
            };
        }
    }

}
