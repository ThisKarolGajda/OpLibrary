package me.opkarol.oplibrary.configurationfile;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@IgnoreInject
public interface IConfigurationFile {

    void createConfig();

    void createNewEmptyFile();

    void updateConfig();

    File getFile();

    FileConfiguration getFileConfiguration();

    void save();

    void reload();

    void set(String path, Object object);

    Object get(String path);
}
