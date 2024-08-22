package me.opkarol.oplibrary;

import me.opkarol.oplibrary.autostart.OpAutoDisable;
import me.opkarol.oplibrary.commands.CommandRegister;
import me.opkarol.oplibrary.commands.annotations.Command;
import me.opkarol.oplibrary.database.DatabaseEntity;
import me.opkarol.oplibrary.database.manager.Database;
import me.opkarol.oplibrary.database.manager.DatabaseFactory;
import me.opkarol.oplibrary.database.manager.DatabaseHolder;
import me.opkarol.oplibrary.debug.PluginDebugger;
import me.opkarol.oplibrary.injection.DependencyInjection;
import me.opkarol.oplibrary.injection.config.ConfigInjectionManager;
import me.opkarol.oplibrary.injection.config.ConfigManager;
import me.opkarol.oplibrary.injection.formatter.DefaultTextFormatter;
import me.opkarol.oplibrary.injection.inventories.injection.InventoriesInjectionManager;
import me.opkarol.oplibrary.injection.inventories.injection.InventoriesManager;
import me.opkarol.oplibrary.injection.messages.MessagesInjectionManager;
import me.opkarol.oplibrary.injection.messages.MessagesManager;
import me.opkarol.oplibrary.injection.wrapper.ColorWrapper;
import me.opkarol.oplibrary.inventories.ItemBuilder;
import me.opkarol.oplibrary.listeners.Listener;
import me.opkarol.oplibrary.location.OpLocation;
import me.opkarol.oplibrary.runnable.OpRunnable;
import me.opkarol.oplibrary.runnable.OpTimerRunnable;
import me.opkarol.oplibrary.tools.FormatTool;
import me.opkarol.oplibrary.tools.Heads;
import me.opkarol.oplibrary.tools.MathUtils;
import me.opkarol.oplibrary.util.ClassFinder;
import me.opkarol.oplibrary.wrappers.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Plugin extends JavaPlugin implements PluginSettings {
    private static Plugin instance;
    private final CommandRegister commandRegister = new CommandRegister();
    private Metrics metrics;

    {
        instance = this;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static <T> T get(Class<T> clazz) {
        return DependencyInjection.get(clazz);
    }

    public static void reload() {
        DependencyInjection.autoInject();
        MessagesInjectionManager.autoInject();
        ConfigInjectionManager.autoInject();
    }

    public static @NotNull String format(String message) {
        return FormatTool.formatMessage(message);
    }

    public static @NotNull List<String> format(List<String> list) {
        return FormatTool.formatList(list);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable Object toFormattedObject(@NotNull Object object) {
        return switch (object) {
            case String string -> FormatTool.formatMessage(string);
            case List<?> list -> {
                if (list.stream().allMatch(obj -> obj instanceof String s)) {
                    yield FormatTool.formatList((List<String>) object);
                }

                yield null;
            }
            default -> null;
        };
    }

    @SuppressWarnings("unchecked")
    public static @Nullable String toFormattedString(@NotNull Object object) {
        return switch (object) {
            case String string -> FormatTool.formatMessage(string);
            case List<?> list -> {
                if (list.stream().allMatch(obj -> obj instanceof String s)) {
                    yield String.join("\n", FormatTool.formatList((List<String>) object));
                }

                yield null;
            }
            default -> null;
        };
    }

    public static int getRandom(int min, int max) {
        return MathUtils.getRandomInt(min, max);
    }

    public static double getRandom(double min, double max) {
        return MathUtils.getRandomDouble(min, max);
    }

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

    public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> getFactory(@NotNull Class<T> clazz, Class<T[]> clazzArray) {
        String lastPartOfClassName = clazz.getSimpleName().toLowerCase() + ".db";
        return DatabaseFactory.createJSON(lastPartOfClassName, clazz, clazzArray, false);
    }


    public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> getJSONDatabase(Class<T> clazz, Class<T[]> classArray, String fileName) {
        return DatabaseFactory.createJSON(fileName, clazz, classArray, false);
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void onDisable() {
        disable();
        OpAutoDisable.registerDisable();
        DependencyInjection.dispose();
    }

    public abstract void disable();

    public void onEnable() {
        registerConfigurationSerialization();

        getLogger().info("Plugin initialization");
        DependencyInjection.register(this);

        DependencyInjection.register(new PluginDebugger(this));
        DependencyInjection.registerInject(new ClassFinder(this));
        DependencyInjection.autoInject(PluginDebugger.class);

        getLogger().info(getClass().getName() + " initialization");
        DependencyInjection.autoInject(this.getClass());

        getLogger().info("Files initialization");
        DependencyInjection.registerInject(new ConfigManager(this));
        DependencyInjection.registerInject(new MessagesManager(this));
        DependencyInjection.registerInject(new InventoriesManager(this));
        DependencyInjection.registerInject(new DefaultTextFormatter());
        ConfigInjectionManager.autoInject();
        MessagesInjectionManager.autoInject();
        InventoriesInjectionManager.autoInject();

        // Listeners and Commands
        getLogger().info("Registering Databases, Listeners and Commands");
        for (Class<?> clazz : get(ClassFinder.class).findAllClassesUsingClassLoader()) {
            // Database
            if (Database.class.isAssignableFrom(clazz)) {
                try {
                    DependencyInjection.registerInject(clazz.getDeclaredConstructor().newInstance());
                } catch (Exception ignore) {
                }
            }

            // Listener
            if (Listener.class.isAssignableFrom(clazz)) {
                try {
                    clazz.getDeclaredConstructor().newInstance();
                } catch (Exception ignore) {
                }
            }

            // Command
            if (clazz.isAnnotationPresent(Command.class)) {
                registerCommand(clazz);
            }
        }

        getLogger().info("Initializing constructors");
        DependencyInjection.initializeConstructors();

        getLogger().info("Enabling plugin");
        enable();
        getLogger().info("Registering bstats");
        registerBStats();
    }

    private void registerConfigurationSerialization() {
        getLogger().info("Registering configuration serialization");
        ConfigurationSerialization.registerClass(ColorWrapper.class);
        ConfigurationSerialization.registerClass(ItemBuilder.class);
        ConfigurationSerialization.registerClass(OpLocation.class);
        ConfigurationSerialization.registerClass(OpActionBar.class);
        ConfigurationSerialization.registerClass(OpBossBar.class);
        ConfigurationSerialization.registerClass(OpParticle.class);
        ConfigurationSerialization.registerClass(OpSound.class);
        ConfigurationSerialization.registerClass(OpTitle.class);
        getLogger().info("Registered configuration serialization");
    }

    private void registerBStats() {
        Integer bstats = registerBStatsOnStartup();
        if (bstats != null) {
            metrics = new Metrics(this, bstats);
        }
    }

    public abstract void enable();

    public <T> void register(Class<T> clazz, T t) {
        DependencyInjection.register(t);
    }

    public <T> void register(T t) {
        if (t instanceof Class) {
            getLogger().info("Cannot register class as object. Use register(Class<T> clazz, T t) instead.");
            return;
        }

        DependencyInjection.register(t);
    }

    public void registerCommand(Class<?> clazz) {
        getCommandRegister().registerClass(clazz);
    }

    public ItemBuilder head(String texture) {
        return Heads.get(texture);
    }

    public ItemBuilder head(OfflinePlayer player) {
        return Heads.get(player);
    }
}
