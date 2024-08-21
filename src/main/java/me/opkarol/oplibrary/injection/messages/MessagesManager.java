package me.opkarol.oplibrary.injection.messages;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.injection.file.FileManager;
import org.jetbrains.annotations.NotNull;

public class MessagesManager {
    private final FileManager fileManager;

    public MessagesManager(Plugin plugin) {
        this.fileManager = new FileManager(plugin, "messages.yml");
    }

    public @NotNull String getMessage(String key, String defaultValue) {
        return fileManager.getValue(key, defaultValue);
    }

    public @NotNull String getMessage(String key) {
        return fileManager.getValue(key, "");
    }

    public void setMessage(String key, String message) {
        fileManager.setValue(key, message);
    }

    @SuppressWarnings("unused")
    public void reload() {
        fileManager.reload();
    }
}