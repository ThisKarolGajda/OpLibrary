package me.opkarol.oplibrary.injection.config;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.injection.file.FileManager;

public class ConfigManager {
    private final FileManager fileManager;

    public ConfigManager(Plugin plugin) {
        this.fileManager = new FileManager(plugin, "config.yml");
    }

    public <C> C get(String key, C defaultObject) {
        return fileManager.getValue(key, defaultObject);
    }

    public void set(String key, Object object) {
        fileManager.setValue(key, object);
    }

    @SuppressWarnings("unused")
    public void reload() {
        fileManager.reload();
    }
}