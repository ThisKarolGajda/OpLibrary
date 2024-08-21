package me.opkarol.oplibrary.inventories;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.tools.FormatTool;
import me.opkarol.oplibrary.tools.PDCTools;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("all")
public class ItemBuilder extends ItemStack implements Serializable, Cloneable {
    private final Map<String, String> tempReplacements = new HashMap<>();

    public ItemBuilder() {
        super();
    }

    public ItemBuilder(@NotNull Material type) {
        super(type);
    }

    public ItemBuilder(@NotNull Material type, int amount) {
        super(type, amount);
    }

    public ItemBuilder(@NotNull ItemStack stack) {
        super(stack);
    }

    public ItemBuilder applyPDC(String key, String value) {
        PDCTools.addNBT(this, new NamespacedKey(Plugin.getInstance(), key), value);
        return this;
    }

    public ItemBuilder removePDC(String key) {
        PDCTools.removeNBT(this, new NamespacedKey(Plugin.getInstance(), key));
        return this;
    }

    public ItemBuilder setEnchants(@NotNull Map<Enchantment, Integer> enchantments) {
        ItemMeta meta = getItemMeta();
        if (meta != null) {
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setFlags(ItemFlag... itemFlags) {
        ItemMeta meta = getItemMeta();
        if (meta != null) {
            meta.addItemFlags(itemFlags);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setName(String name) {
        return useItemMeta(meta -> meta.setDisplayName(FormatTool.formatMessage(replacePlaceholders(name))));
    }

    public List<String> getLore() {
        ItemMeta meta = getItemMeta();
        return meta != null ? meta.getLore() : new ArrayList<>();
    }

    public ItemBuilder setLore(@NotNull List<String> lore) {
        return useItemMeta(meta -> meta.setLore(FormatTool.formatList(replaceLorePlaceholders(lore))));
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(List.of(lore));
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            setEnchants(Map.of(Enchantment.LURE, 1));
            setFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    private ItemBuilder useItemMeta(@NotNull Consumer<ItemMeta> action) {
        ItemMeta meta = getItemMeta();
        if (meta != null) {
            action.accept(meta);
            setItemMeta(meta);
        }
        return this;
    }

    private String replacePlaceholders(String text) {
        for (Map.Entry<String, String> entry : tempReplacements.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

    private @NotNull List<String> replaceLorePlaceholders(@NotNull List<String> lore) {
        List<String> updatedLore = new ArrayList<>();
        for (String line : lore) {
            updatedLore.add(replacePlaceholders(line));
        }
        return updatedLore;
    }

    public ItemStack generate() {
        return (ItemStack) super.clone();
    }
}