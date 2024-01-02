package me.opkarol.oplibrary.translations;

import me.opkarol.oplibrary.configurationfile.ConfigurationFile;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TranslationManager {
    private final Map<String, String> translations;
    private final ConfigurationFile configurationFile;

    public TranslationManager(Plugin plugin, String fileName) {
        this.translations = new HashMap<>();
        configurationFile = new ConfigurationFile(plugin, fileName);
        configurationFile.safeUpdate();
        loadTranslations();
    }

    private void loadTranslations() {
        configurationFile.getFileConfiguration().getKeys(true).forEach(key -> {
            String translation = configurationFile.getFileConfiguration().getString(key);
            translations.put(key, FormatTool.formatMessage(translation));
        });
    }

    public String getTranslation(String key) {
        return translations.getOrDefault(key, "Translation not found for key: " + key);
    }

    public String getFormattedTranslation(String key) {
        return FormatTool.formatMessage(translations.getOrDefault(key, "Translation not found for key: " + key));
    }

    public void sendGlobalMessage(String key) {
        String message = getFormattedTranslation(key);
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(message, player);
        }
    }

    private void send(@NotNull String message, Player player) {
        String[] split = message.split("\\{NEW}");
        for (String part : split) {
            player.sendMessage(part);
        }

        // Send empty line if message ends with it
        if (message.endsWith("{NEW}")) {
            player.sendMessage("\n");
        }
    }

    public void sendGlobalMessage(String key, @NotNull Map<String, String> replacements) {
        String translation = getTranslation(key);

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            translation = translation.replace(entry.getKey(), entry.getValue());
        }

        String message = FormatTool.formatMessage(translation);
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(message, player);
        }
    }

    public void sendMessage(String key, @NotNull Player player) {
        send(getFormattedTranslation(key), player);
    }

    public void sendMessage(String key, Player player, @NotNull Map<String, String> replacements) {
        String translation = getTranslation(key);

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            translation = translation.replace(entry.getKey(), entry.getValue());
        }

        send(FormatTool.formatMessage(translation), player);
    }

    public void reload() {
        translations.clear();
        configurationFile.reload();
        loadTranslations();
    }
}