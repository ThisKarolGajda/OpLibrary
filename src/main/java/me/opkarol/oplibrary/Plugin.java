package me.opkarol.oplibrary;

import me.opkarol.oplibrary.autostart.OpAutoDisable;
import me.opkarol.oplibrary.commands.CommandRegister;
import me.opkarol.oplibrary.configurationfile.ConfigurationFile;
import me.opkarol.oplibrary.inventories.InventoryListener;
import me.opkarol.oplibrary.runnable.OpRunnable;
import me.opkarol.oplibrary.translations.Messages;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public abstract class Plugin extends JavaPlugin implements PluginSettings {
    private static Plugin instance;
    private final DependencyManager dependencyManager = new DependencyManager();
    private final CommandRegister commandRegister = new CommandRegister();
    private final Messages messages = new Messages(this);
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

    public static <T> T get(Class<T> clazz) {
        return getDependency().get(clazz);
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }

    public ConfigurationFile getInventoriesFile() {
        return inventoriesFile;
    }

    public Messages getMessagesManager() {
        return messages;
    }

    public static Messages getMessages() {
        return Plugin.getInstance().getMessagesManager();
    }

    public ConfigurationFile getConfigurationFile() {
        return configurationFile;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void onDisable() {
        disable();
        OpAutoDisable.registerDisable();
        getDependencyManager().dispose();
    }

    public abstract void disable();

    public void onEnable() {
        new InventoryListener().runListener();
        getConfigurationFile().safeUpdate();
        getInventoriesFile().safeUpdate();
        if (registerBStatsOnStartup() != null) {
            metrics = new Metrics(this, registerBStatsOnStartup());
        }
        enable();
    }

    public abstract void enable();

    public <T> void register(Class<T> clazz, T t) {
        getDependencyManager().register(clazz, t);
    }

    public <T> void register(T t) {
        getDependencyManager().register((Class<T> )t.getClass(), t);
    }

    public void registerCommand(Class<?> clazz) {
        getCommandRegister().registerClass(clazz);
    }

    public static void reload() {
        Plugin.getInstance().getConfigurationFile().reload();
        Plugin.getInstance().getInventoriesFile().reload();
        Messages.reload();
    }

    public static OpRunnable run(Runnable runnable) {
        return new OpRunnable(runnable).runTask();
    }

    public static OpRunnable runLater(Runnable runnable, long delay) {
        return new OpRunnable(runnable).runTaskLater(delay);
    }

    public static OpRunnable runTimer(Runnable runnable, long delay) {
        return new OpRunnable(runnable).runTaskTimer(delay);
    }

    public static OpRunnable runAsync(Runnable runnable) {
        return new OpRunnable(runnable).runTaskAsynchronously();
    }

    public static OpRunnable runLaterAsync(Runnable runnable, long delay) {
        return new OpRunnable(runnable).runTaskLaterAsynchronously(delay);
    }

    public static OpRunnable runTimerAsync(Runnable runnable, long delay) {
        return new OpRunnable(runnable).runTaskTimerAsynchronously(delay);
    }
}
