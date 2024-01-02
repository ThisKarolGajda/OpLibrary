package me.opkarol.oplibrary.configurationfile;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.Set;

public interface IConfigurationFileHelper extends IConfigurationFile {
    default Optional<ConfigurationSection> getConfigurationSection(String path) {
        if (getFileConfiguration() == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(getFileConfiguration()
                .getConfigurationSection(path.endsWith(".") ? path.substring(0, path.length() - 1) : path));
    }

    default Optional<Set<String>> getConfigurationSectionKeys(String path) {
        Optional<ConfigurationSection> optional = getConfigurationSection(path);
        return optional.map(section -> section.getKeys(false));
    }
}
