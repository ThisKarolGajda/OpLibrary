package me.opkarol.oplibrary;

import me.opkarol.oplibrary.autostart.OpAutoDisable;
import me.opkarol.oplibrary.commands.CommandRegister;
import me.opkarol.oplibrary.configurationfile.ConfigurationFile;
import me.opkarol.oplibrary.database.DatabaseTypesCache;
import me.opkarol.oplibrary.database.manager.DatabaseFactory;
import me.opkarol.oplibrary.database.manager.DatabaseHolder;
import me.opkarol.oplibrary.inventories.InventoryListener;
import me.opkarol.oplibrary.inventories.ItemBuilder;
import me.opkarol.oplibrary.runnable.OpRunnable;
import me.opkarol.oplibrary.runnable.OpTimerRunnable;
import me.opkarol.oplibrary.tools.FormatTool;
import me.opkarol.oplibrary.tools.Heads;
import me.opkarol.oplibrary.tools.MathUtils;
import me.opkarol.oplibrary.translations.Messages;
import me.opkarol.oplibrary.wrappers.*;
import me.opkarol.oporm.DatabaseEntity;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Plugin extends JavaPlugin implements PluginSettings {
    private static Plugin instance;
    private final DependencyManager dependencyManager = new DependencyManager();
    private final CommandRegister commandRegister = new CommandRegister();
    private final Messages messages = new Messages(this);
    private final ConfigurationFile inventoriesFile = new ConfigurationFile(this, "inventories.yml");
    private final ConfigurationFile configurationFile = new ConfigurationFile(this, "config.yml");
    private final DatabaseTypesCache databaseCache = new DatabaseTypesCache();
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
        enable();
        registerBStats();
    }

    private void registerBStats() {
        Integer bstats = registerBStatsOnStartup();
        if (bstats != null) {
            metrics = new Metrics(this, bstats);
        }
    }

    public abstract void enable();

    public <T> void register(Class<T> clazz, T t) {
        getDependencyManager().register(clazz, t);
    }

    public <T> void register(T t) {
        if (t instanceof Class) {
            getLogger().info("Cannot register class as object. Use register(Class<T> clazz, T t) instead.");
            return;
        }

        getDependencyManager().register((Class<T>) t.getClass(), t);
    }

    public void registerCommand(Class<?> clazz) {
        getCommandRegister().registerClass(clazz);
    }

    public static void reload() {
        Plugin.getInstance().getConfigurationFile().reload();
        Plugin.getInstance().getInventoriesFile().reload();
        Messages.reload();
    }

    public DatabaseTypesCache getDatabaseCache() {
        return databaseCache;
    }

    public static @NotNull String format(String message) {
        return FormatTool.formatMessage(message);
    }

    public static @NotNull List<String> format(List<String> list) {
        return FormatTool.formatList(list);
    }

    public static Object toFormattedObject(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof String string) {
            return FormatTool.formatMessage(string);
        }

        if (object instanceof List<?> list) {
            if (list.stream().allMatch(obj -> obj instanceof String s)) {
                return FormatTool.formatList((List<String>) object);
            }
        }

        return null;
    }

    public static String toFormattedString(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof String string) {
            return FormatTool.formatMessage(string);
        }

        if (object instanceof List<?> list) {
            if (list.stream().allMatch(obj -> obj instanceof String s)) {
                return String.join("\n", FormatTool.formatList((List<String>) object));
            }
        }

        return null;
    }

    public static int getRandom(int min, int max) {
        return MathUtils.getRandomInt(min, max);
    }

    public static double getRandom(double min, double max) {
        return MathUtils.getRandomDouble(min, max);
    }

    public ItemBuilder head(String texture) {
        return Heads.get(texture);
    }

    public ItemBuilder head(OfflinePlayer player) {
        return Heads.get(player);
    }

    public static class Timer {
        public static @NotNull OpRunnable run(Runnable runnable) {
            return new OpRunnable(runnable).runTask();
        }

        public static @NotNull OpRunnable runLater(Runnable runnable, long delay) {
            return new OpRunnable(runnable).runTaskLater(delay);
        }

        public static @NotNull OpRunnable runLater(Consumer<OpRunnable> consumer, long delay) {
            return new OpRunnable(consumer).runTaskLater(delay);
        }

        public static @NotNull OpRunnable runTimer(Runnable runnable, long delay) {
            return new OpRunnable(runnable).runTaskTimer(delay);
        }

        public static @NotNull OpRunnable runTimer(Consumer<OpRunnable> consumer, long delay) {
            return new OpRunnable(consumer).runTaskTimer(delay);
        }

        public static @NotNull OpRunnable runAsync(Runnable runnable) {
            return new OpRunnable(runnable).runTaskAsynchronously();
        }

        public static @NotNull OpRunnable runAsync(Consumer<OpRunnable> consumer) {
            return new OpRunnable(consumer).runTaskAsynchronously();
        }

        public static @NotNull OpRunnable runLaterAsync(Runnable runnable, long delay) {
            return new OpRunnable(runnable).runTaskLaterAsynchronously(delay);
        }

        public static @NotNull OpRunnable runLaterAsync(Consumer<OpRunnable> consumer, long delay) {
            return new OpRunnable(consumer).runTaskLaterAsynchronously(delay);
        }

        public static @NotNull OpRunnable runTimerAsync(Runnable runnable, long delay) {
            return new OpRunnable(runnable).runTaskTimerAsynchronously(delay);
        }

        public static @NotNull OpRunnable runTimerAsync(Consumer<OpRunnable> consumer, long delay) {
            return new OpRunnable(consumer).runTaskTimerAsynchronously(delay);
        }

        public static void runTimes(Runnable runnable, int times) {
            new OpTimerRunnable().run(runnable, times);
        }

        public static void runTimes(Consumer<OpRunnable> consumer, int times) {
            new OpTimerRunnable().run(consumer, times);
        }

        public static void runTimes(Runnable runnable, int times, int delay) {
            new OpTimerRunnable().run(runnable, times, delay);
        }

        public static void runTimes(Consumer<OpRunnable> consumer, int times, int delay) {
            new OpTimerRunnable().run(consumer, times, delay);
        }
    }

    public static class Effects {
        public static void actionBar(Player player, String text) {
            new OpActionBar(text)
                    .send(player);
        }

        public static void actionBar(Player player, String text, int times) {
            new OpActionBar(text)
                    .sendLooped(times)
                    .addReceiver(player);
        }

        public static void actionBar(Player player, String text, List<Player> players) {
            new OpActionBar(text)
                    .setReceivers(players)
                    .send();
        }

        public static void actionBar(Player player, String text, List<Player> players, int times) {
            new OpActionBar(text)
                    .setReceivers(players)
                    .sendLooped(times);
        }

        public static void actionBar(Player player, @NotNull OpActionBar actionBar) {
            actionBar.send(player);
        }

        public static void actionBar(Player player, @NotNull OpActionBar actionBar, int times) {
            actionBar.sendLooped(times)
                    .addReceiver(player);
        }

        public static void actionBar(Player player, @NotNull OpActionBar actionBar, List<Player> players) {
            actionBar.setReceivers(players)
                    .send();
        }

        public static void actionBar(Player player, @NotNull OpActionBar actionBar, List<Player> players, int times) {
            actionBar.setReceivers(players)
                    .sendLooped(times);
        }

        public static void highlightBlock(Player player, Block block, Particle particle) {
            new OpBlockHighlighter(block, particle)
                    .highlightFor(player);
        }

        public static void highlightBlock(Player player, Location location, Particle particle) {
            new OpBlockHighlighter(location, particle)
                    .highlightFor(player);
        }

        public static void bossBar(Player player, BarStyle style, BarColor color) {
            new OpBossBar()
                    .setBarStyle(style)
                    .setBarColor(color)
                    .display(player);
        }

        public static void bossBar(Player player, BarStyle style, BarColor color, String title) {
            new OpBossBar()
                    .setBarStyle(style)
                    .setBarColor(color)
                    .setTitle(title)
                    .display(player);
        }

        public static void bossBar(Player player, BarStyle style, BarColor color, String title, int ticks) {
            new OpBossBar()
                    .setBarStyle(style)
                    .setBarColor(color)
                    .setTitle(title)
                    .displayAndRemoveAfter(List.of(player), ticks);
        }

        public static void bossBar(List<Player> players, BarStyle style, BarColor color, String title, int ticks) {
            new OpBossBar()
                    .setBarStyle(style)
                    .setBarColor(color)
                    .setTitle(title)
                    .displayAndRemoveAfter(players, ticks);
        }

        public static void bossBar(List<Player> players, BarStyle style, BarColor color, String title) {
            new OpBossBar()
                    .setBarStyle(style)
                    .setBarColor(color)
                    .setTitle(title)
                    .display(players);
        }

        public static void bossBar(List<Player> players, BarStyle style, BarColor color) {
            new OpBossBar()
                    .setBarStyle(style)
                    .setBarColor(color)
                    .display(players);
        }

        public static void bossBar(List<Player> players, BarStyle style, BarColor color, int ticks) {
            new OpBossBar()
                    .setBarStyle(style)
                    .setBarColor(color)
                    .displayAndRemoveAfter(players, ticks);
        }

        public static void particle(Player player, Particle particle, Location location) {
            new OpParticle(particle)
                    .setLocation(location)
                    .display(player);
        }

        public static void particle(Player player, Particle particle, Location location, int amount) {
            new OpParticle(particle)
                    .setLocation(location)
                    .setAmount(amount)
                    .display(player);
        }

        public static void particle(Particle particle, Location location, int amount) {
            new OpParticle(particle)
                    .setLocation(location)
                    .setAmount(amount)
                    .displayForAllOnline();
        }

        public static void particle(Player player, Particle particle, Location location, int amount, float offsetX, float offsetY, float offsetZ) {
            new OpParticle(particle)
                    .setLocation(location)
                    .setAmount(amount)
                    .setOffsetX(offsetX)
                    .setOffsetY(offsetY)
                    .setOffsetZ(offsetZ)
                    .display(player);
        }

        public static void particle(Particle particle, Location location, int amount, float offsetX, float offsetY, float offsetZ) {
            new OpParticle(particle)
                    .setLocation(location)
                    .setAmount(amount)
                    .setOffsetX(offsetX)
                    .setOffsetY(offsetY)
                    .setOffsetZ(offsetZ)
                    .displayForAllOnline();
        }

        public static void particle(Player player, @NotNull OpParticle particle, int amount) {
            particle.setAmount(amount)
                    .display(player);
        }

        public static void sound(Player player, Sound sound) {
            new OpSound(sound)
                    .play(player);
        }

        public static void sound(Player player, Sound sound, float volume, float pitch) {
            new OpSound(sound)
                    .setVolume(volume)
                    .setPitch(pitch)
                    .play(player);
        }


        public static void sound(Location location, Sound sound) {
            new OpSound(sound)
                    .play(location);
        }

        public static void title(Player player, String title) {
            new OpTitle()
                    .setTitle(title)
                    .display(player);
        }

        public static void title(Player player, String title, String subTitle) {
            new OpTitle()
                    .setTitle(title)
                    .setSubtitle(subTitle)
                    .display(player);
        }

        public static void title(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
            new OpTitle()
                    .setTitle(title)
                    .setSubtitle(subTitle)
                    .setFadeIn(fadeIn)
                    .setStay(stay)
                    .setFadeOut(fadeOut)
                    .display(player);
        }

        public static void title(List<Player> players, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
            new OpTitle()
                    .setTitle(title)
                    .setSubtitle(subTitle)
                    .setFadeIn(fadeIn)
                    .setStay(stay)
                    .setFadeOut(fadeOut)
                    .setReceivers(players)
                    .display();
        }
    }

    public static class Database {
        public static <PK extends Serializable, T extends DatabaseEntity<PK>> DatabaseHolder<PK, T> getFactory(Class<T> clazz, Class<T[]> clazzArray) {
            ConfigurationFile config = Plugin.getInstance().getConfigurationFile();
            String databaseType = config.getFileConfiguration().getString("databaseType");
            String lastPartOfClassName = clazz.getSimpleName().toLowerCase() + ".db";

            if ("MYSQL".equals(databaseType)) {
                String mysqlUrl = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true",
                        config.getFileConfiguration().getString("connectionSettings.host"),
                        config.getFileConfiguration().getString("connectionSettings.port"),
                        config.getFileConfiguration().getString("connectionSettings.database"));
                String username = config.getFileConfiguration().getString("connectionSettings.username");
                String password = config.getFileConfiguration().getString("connectionSettings.password");
                return DatabaseFactory.createSql(mysqlUrl, username, password, clazz);
            } else if ("JSON".equals(databaseType) || databaseType == null) {
                return DatabaseFactory.createJSON(lastPartOfClassName, clazz, clazzArray, false);
            } else {
                return DatabaseFactory.createFlat(Plugin.getInstance(), lastPartOfClassName);
            }
        }


        @Contract("_ -> new")
        public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> getFlatDatabase(String fileName) {
            return DatabaseFactory.createFlat(Plugin.getInstance(), fileName);
        }

        public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> getJSONDatabase(Class<T> clazz, Class<T[]> classArray, String fileName) {
            return DatabaseFactory.createJSON(fileName, clazz, classArray, false);
        }

        @Contract("_, _, _, _ -> new")
        public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> getSQLDatabase(Class<T> clazz, String url, String host, String password) {
            return DatabaseFactory.createSql(url, host, password, clazz);
        }
    }

    public static ConfigurationFile getConfiguration() {
        return getInstance().getConfigurationFile();
    }
}
