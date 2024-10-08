package me.opkarol.oplibrary.tools;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
@IgnoreInject
public class MathUtils {

    public static int getRandomInt(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) {
            return;
        }
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null || type != itemStack.getType()) {
                continue;
            }
            int newAmount = itemStack.getAmount() - amount;
            if (newAmount > 0) {
                itemStack.setAmount(newAmount);
                break;
            } else {
                inventory.clear(slot);
                amount = -newAmount;
                if (amount == 0) {
                    break;
                }
            }
        }
    }

    public static String convertToRomanNumber(int num) {
        if (num <= 0 || num > 3999) {
            throw new IllegalArgumentException("Number must be between 1 and 3999");
        }

        StringBuilder roman = new StringBuilder();
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                roman.append(symbols[i]);
                num -= values[i];
            }
        }

        return roman.toString();
    }

    public static double getRandomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }
}
