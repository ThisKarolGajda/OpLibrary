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
    }

    /**
     * Defaults stack size to 1, with no extra data.
     * <p>
     * <b>IMPORTANT: An <i>Item</i>Stack is only designed to contain
     * <i>items</i>. Do not use this class to encapsulate Materials for which
     * {@link Material#isItem()} returns false.</b>
     *
     * @param type item material
     */
    public ItemBuilder(@NotNull Material type) {
        super(type);
    }

    /**
     * An item stack with no extra data.
     * <p>
     * <b>IMPORTANT: An <i>Item</i>Stack is only designed to contain
     * <i>items</i>. Do not use this class to encapsulate Materials for which
     * {@link Material#isItem()} returns false.</b>
     *
     * @param type   item material
     * @param amount stack size
     */
    public ItemBuilder(@NotNull Material type, int amount) {
        super(type, amount);
    }

    /**
     * Creates a new item stack derived from the specified stack
     *
     * @param stack the stack to copy
     * @throws IllegalArgumentException if the specified stack is null or
     *                                  returns an item meta not created by the item factory
     */
    public ItemBuilder(@NotNull ItemStack stack) throws IllegalArgumentException {
        super(stack);
    }

    public void checkItemMeta() {
        if (getItemMeta() == null) {
            throw new RuntimeException("Item meta is invalid. May enable exploits.");
        }
    }

    public ItemBuilder setLore(@NotNull List<String> lore) {
        useItemMeta(meta -> {
            List<String> tempLore = new ArrayList<>();
            for (String line : lore) {
                for (String replace : tempReplacements.keySet()) {
                    line = line.replace(replace, tempReplacements.get(replace));
                }
                tempLore.add(line);
            }
            meta.setLore(FormatTool.formatList(tempLore));
        });
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(List.of(lore));
    }

    public ItemBuilder setName(String name) {
        useItemMeta(meta -> {
            String tempName = name;
            for (String replace : tempReplacements.keySet()) {
                tempName = tempName.replace(replace, tempReplacements.get(replace));
            }
            meta.setDisplayName(FormatTool.formatMessage(tempName));
        });
        return this;
    }

    public ItemBuilder applyPDC(String key, String value) {
        PDCTools.addNBT(this, new NamespacedKey(Plugin.getInstance(), key), value);
        return this;
    }

    public ItemBuilder secure() {
        return new ItemBuilder(SecureItem.applySafeItem(this));
    }

    public ItemBuilder setEnchants(@NotNull Map<Enchantment, Integer> enchantments) {
        checkItemMeta();
        enchantments.forEach((enchantment, level) ->
                useItemMeta(meta ->
                        meta.addEnchant(enchantment, level, true)));
        return this;
    }

    public ItemBuilder setFlags(ItemFlag... itemFlag) {
        useItemMeta(meta -> meta.addItemFlags(itemFlag));
        return this;
    }

    public ItemBuilder setAllFlags() {
        return setFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
    }

    private void useItemMeta(@NotNull Consumer<ItemMeta> action) {
        checkItemMeta();
        ItemMeta meta = getItemMeta();
        action.accept(meta);
        setItemMeta(meta);
    }

    public void updateLoreView() {
        if (getLore() != null) {
            setLore(getLore());
        }
    }

    public ItemBuilder amount(int amount) {
        setAmount(amount);
        return this;
    }

    /**
     * Replacements
    /*/

    public ItemBuilder addReplacement(String replace, String replacement) {
        tempReplacements.put(replace, replacement);
        return this;
    }

    public ItemBuilder addReplacement(@NotNull Map<String, String> map) {
        map.forEach((replace, replacement) -> {
            addReplacement(replace, replacement);
        });
        return this;
    }

    /**
     * Getters
     */

    public String getName() {
        checkItemMeta();
        return getItemMeta().getDisplayName();
    }

    public List<String> getLore() {
        checkItemMeta();
        return getItemMeta().getLore();
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            if (getEnchantments() != null && getEnchantments().size() == 0) {
                setEnchants(Map.of(Enchantment.LURE, 1));
            }

            setAllFlags();
        }

        return this;
    }

    public Map<String, String> getTempReplacements() {
        return tempReplacements;
    }

    public ItemStack generate() {
        ItemBuilder copy = (ItemBuilder) super.clone();
        if (copy.getLore() != null && tempReplacements.keySet().size() != 0) {
            ItemMeta meta = copy.getItemMeta();
            List<String> tempLore = new ArrayList<>();
            for (String line : copy.getLore()) {
                for (String replace : tempReplacements.keySet()) {
                    line = line.replace(replace, tempReplacements.get(replace));
                }
                tempLore.add(line);
            }
            meta.setLore(FormatTool.formatList(tempLore));
            copy.setItemMeta(meta);
        }
        return copy;
    }
}
