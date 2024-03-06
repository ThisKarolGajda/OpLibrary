package me.opkarol.oplibrary.tools;

import me.opkarol.oplibrary.inventories.ItemBuilder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Heads {

    public static ItemBuilder get(String minecraftUrlValue) {
        return HeadManager.getHeadFromMinecraftValueUrl(minecraftUrlValue);
    }

    @Contract("_ -> new")
    public static @NotNull ItemBuilder get(OfflinePlayer player) {
        return new ItemBuilder(HeadManager.getHeadValue(player));
    }
}
