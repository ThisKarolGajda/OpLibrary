package me.opkarol.oplibrary.configurationfile;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@IgnoreInject
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

    default void forEachKey(String path, Consumer<String> keyConsumer) {
        getConfigurationSectionKeys(path).ifPresent(keys -> keys.forEach(keyConsumer));
    }
}
