package me.opkarol.oplibrary;

import me.opkarol.oplibrary.autostart.OpAutoDisable;
import me.opkarol.oplibrary.commands.CommandRegister;
import me.opkarol.oplibrary.configurationfile.ConfigurationFile;
import me.opkarol.oplibrary.inventories.InventoryListener;
import me.opkarol.oplibrary.translations.TranslationManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public abstract class Plugin extends JavaPlugin implements PluginSettings {
    private static Plugin instance;
    private final DependencyManager dependencyManager = new DependencyManager();
    private final CommandRegister commandRegister = new CommandRegister();
    private final TranslationManager messagesManager = new TranslationManager(this, "messages.yml");
    private final ConfigurationFile inventoriesFile = new ConfigurationFile(this, "inventories.yml");
    private final ConfigurationFile configurationFile = new ConfigurationFile(this, "config.yml");
    private Metrics metrics;

    {
        instance = this;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public static DependencyManager getDependency() {
        return Plugin.getInstance().getDependencyManager();
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }

    public ConfigurationFile getInventoriesFile() {
        return inventoriesFile;
    }

    public TranslationManager getMessagesManager() {
        return messagesManager;
    }

    public static TranslationManager getMessages() {
        return Plugin.getInstance().getMessagesManager();
    }

    public ConfigurationFile getConfigurationFile() {
        return configurationFile;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void onDisable() {
        OpAutoDisable.registerDisable();
        getDependencyManager().dispose();
    }

    public void onEnable() {
        new InventoryListener().runListener();
        getConfigurationFile().safeUpdate();
        getInventoriesFile().safeUpdate();
        if (registerBStatsOnStartup() != null) {
            metrics = new Metrics(this, registerBStatsOnStartup());
        }
    }
}
