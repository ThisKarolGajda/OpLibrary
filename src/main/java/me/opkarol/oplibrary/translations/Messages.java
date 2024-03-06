package me.opkarol.oplibrary.translations;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("unused")
public class Messages {
    private static Messages instance;
    private final TranslationManager manager;

    public Messages(Plugin plugin) {
        manager = new TranslationManager(plugin, "messages.yml");
        instance = this;
    }

    public TranslationManager getManager() {
        return manager;
    }

    public static Messages getInstance() {
        return instance;
    }

    public static String getTranslation(String key) {
        return getInstance().getManager().getTranslation(key);
    }

    public static String getFormattedTranslation(String key) {
        return getInstance().getManager().getFormattedTranslation(key);
    }

    public static void sendGlobalMessage(String key) {
        getInstance().getManager().sendGlobalMessage(key);
    }

    private static void send(@NotNull String message, Player player) {
        getInstance().getManager().send(message, player);
    }

    public static void sendGlobalMessage(String key, @NotNull Map<String, String> replacements) {
        getInstance().getManager().sendGlobalMessage(key, replacements);
    }

    public static  void sendMessage(String key, @NotNull Player player) {
        send(getFormattedTranslation(key), player);
    }

    public static  void sendMessage(String key, Player player, @NotNull Map<String, String> replacements) {
        getInstance().getManager().sendMessage(key, player, replacements);

    }

    public static void reload() {
        getInstance().getManager().reload();
    }
}
