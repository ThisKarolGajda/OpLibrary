package me.opkarol.oplibrary.injection.file;

import me.opkarol.oplibrary.util.Helper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class FileManager extends Helper {
    private final Plugin plugin;
    private final File configFile;
    private FileConfiguration config;

    public FileManager(@NotNull Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), fileName);
        createConfigFile();
        loadConfig();
    }

    private void createConfigFile() {
        if (!configFile.exists()) {
            try {
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }

                configFile.createNewFile();
                saveConfig();
            } catch (IOException e) {
                debug("Could not create config file: " + configFile.getAbsolutePath());
            }
        }
    }

    private void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public <T> @NotNull T getValue(String key, T defaultType) {
        Object value = config.get(key);
        System.out.println("key: " + key + " value: " + value);
        return (T) (value != null ? value : defaultType);
    }

    public <T> void setValue(String key, T value) {
        config.set(key, value);
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            debug("Could not save config file: " + configFile.getAbsolutePath());
        }
    }

    public void reload() {
        debug("Reloading " + configFile.getName());
        loadConfig();
    }

    public void dispose() {
        config = null;
    }
}