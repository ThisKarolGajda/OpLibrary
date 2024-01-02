package me.opkarol.oplibrary.configurationfile;

import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * The ConfigurationFile class facilitates the management of plugin configuration files in Bukkit/Spigot projects.
 */
public class ConfigurationFile implements IConfigurationFileHelper {

    private final File file;
    private final Plugin plugin;
    private final String fileName;
    private FileConfiguration fileConfiguration;

    /**
     * Constructor for the ConfigurationFile class.
     *
     * @param plugin   The plugin instance.
     * @param fileName The name of the configuration file.
     */
    public ConfigurationFile(Plugin plugin, String fileName) {
        this(plugin, fileName, true);
    }

    /**
     * Constructor for the ConfigurationFile class.
     *
     * @param plugin   The plugin instance.
     * @param fileName The name of the configuration file.
     * @param loadConfiguration Should the YamlConfiguration be loaded on this file.
     */
    public ConfigurationFile(Plugin plugin, String fileName, boolean loadConfiguration) {
        this.fileName = fileName;
        this.plugin = plugin;
        this.file = createFile();
        if (loadConfiguration) {
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
        }
    }

    /**
     * Create the File object representing the configuration file.
     *
     * @return The File object.
     */
    private @NotNull File createFile() {
        return new File(plugin.getDataFolder(), fileName);
    }

    /**
     * Create the configuration file if it doesn't exist.
     * If the plugin's data folder doesn't exist, create it.
     * If the file is not found in the plugin's resources, create a new empty file.
     */
    @Override
    public void createConfig() {
        if (!getFile().exists()) {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            try {
                plugin.saveResource(fileName, false);
                updateConfig();
            } catch (IllegalArgumentException ignore) {
                createNewEmptyFile();
            }
        }
    }

    public void safeUpdate() {
        if (!file.exists() || !file.isFile()) {
            createConfig();
        } else {
            updateConfig();
        }
    }

    /**
     * Create a new empty file.
     */
    @Override
    public void createNewEmptyFile() {
        try {
            if (!file.exists() || !file.isFile()) {
                getFile().createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the configuration file using ConfigUpdater from plugin's resources.
     */
    @Override
    public void updateConfig() {
        try {
            ConfigUpdater.update(plugin, fileName, file);
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
        } catch (IOException ignore) {
            createNewEmptyFile();
        }
    }

    /**
     * Get the File object representing the configuration file.
     *
     * @return The File object.
     */
    @Override
    public File getFile() {
        return file;
    }

    /**
     * Get the FileConfiguration object representing the configuration.
     *
     * @return The FileConfiguration object.
     */
    @Override
    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    /**
     * Save the configuration to the file.
     */
    @Override
    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reload the configuration from the file.
     */
    @Override
    public void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Set a value in the configuration file.
     *
     * @param path   The path to the value.
     * @param object The value to set.
     */
    @Override
    public void set(String path, Object object) {
        if (fileConfiguration != null) {
            fileConfiguration.set(path, object);
        }
    }

    /**
     * Get a value from the configuration file.
     *
     * @param path The path to the value.
     * @return The value or null if not found.
     */
    @Override
    public Object get(String path) {
        if (fileConfiguration != null) {
            return fileConfiguration.get(path);
        }
        return null;
    }
}
