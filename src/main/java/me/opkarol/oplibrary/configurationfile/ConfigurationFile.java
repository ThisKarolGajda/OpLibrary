package me.opkarol.oplibrary.configurationfile;

import com.tchristofferson.configupdater.ConfigUpdater;
import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.location.OpLocation;
import me.opkarol.oplibrary.misc.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The ConfigurationFile class facilitates the management of plugin configuration files in Bukkit/Spigot projects.
 */
@SuppressWarnings("all")
@IgnoreInject
public class ConfigurationFile implements IConfigurationFileHelper {

    private final File file;
    private final Plugin plugin;
    private final String fileName;
    private final Map<String, Object> cache = new HashMap<>();
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
        cache.clear();
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
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        if (fileConfiguration != null) {
            Object object = fileConfiguration.get(path);
            cache.put(path, object);
            return object;
        }

        return null;
    }

    public int getInt(String path) {
        return StringUtil.getInt(get(path));
    }

    public double getDouble(String path) {
        return StringUtil.getDouble(get(path));
    }

    public float getFloat(String path) {
        return StringUtil.getFloat(get(path));
    }

    public OpLocation getLocation(String path) {
        return new OpLocation(getString(path));
    }

    public <K extends Enum<K>> Optional<K> getEnum(String path, Class<K> enumType) {
        return StringUtil.getEnumValue(getString(path), enumType);
    }

    public <K extends Enum<K>> K getUnsafeEnum(String path, Class<K> enumType) {
        return getEnum(path, enumType).orElse(null);
    }

    public <K extends Enum<K>> void useEnumValue(String path, Class<K> clazz, Consumer<K> consumer) {
        StringUtil.getEnumValue(getString(path), clazz).ifPresent(consumer);
    }

    public boolean getBoolean(String path) {
        if (cache.containsKey(path)) {
            return (boolean) cache.get(path);
        }

        if (fileConfiguration != null) {
            boolean object = fileConfiguration.getBoolean(path);
            cache.put(path, object);
            return object;
        }

        return false;
    }

    public String getString(String path) {
        if (cache.containsKey(path)) {
            return (String) cache.get(path);
        }

        if (fileConfiguration != null) {
            String object = fileConfiguration.getString(path);
            cache.put(path, object);
            return object;
        }

        return null;
    }
}
