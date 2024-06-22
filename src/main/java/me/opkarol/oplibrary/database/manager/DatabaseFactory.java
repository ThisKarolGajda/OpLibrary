package me.opkarol.oplibrary.database.manager;

import me.opkarol.oplibrary.database.FlatDatabase;
import me.opkarol.oplibrary.database.JSONDatabase;
import me.opkarol.oplibrary.database.SQLDatabase;
import me.opkarol.oporm.DatabaseEntity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;

public final class DatabaseFactory<PK extends Serializable, T extends DatabaseEntity<PK>> extends DatabaseHolder<PK, T> {

    /**
     * Objects:
     * SQL: url, host, password, class,
     * FLAT: plugin, fileName
     *
     * @param settings Database settings
     */
    public DatabaseFactory(@NotNull DatabaseSettings settings) {
        super(settings.type() == DatabaseSettings.Type.SQL
                ? new SQLDatabase<>(settings.objects().get("url").toString(), settings.objects().get("host").toString(), settings.objects().get("password").toString(), (Class<T>) settings.objects().get("class"))
                : settings.type() == DatabaseSettings.Type.JSON ? new JSONDatabase<>((String) settings.objects().get("fileName"), (Class<T>) settings.objects().get("class"), (Class<T[]>) settings.objects().get("class"), false) : new FlatDatabase<>((Plugin) settings.objects().get("plugin"), (String) settings.objects().get("fileName")));
    }

    public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> createFlat(Plugin plugin, String fileName) {
        return new DatabaseHolder<>(new FlatDatabase<>(plugin, fileName));
    }

    public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> createSql(String url, String host, String password, Class<T> clazz) {
        return new DatabaseHolder<>(new SQLDatabase<>(url, host, password, clazz));
    }

    public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> createJSON(String fileName, Class<T> clazz, Class<T[]> classArray, boolean useMultiFiles) {
        return new DatabaseHolder<>(new JSONDatabase<>(fileName, clazz, classArray, useMultiFiles));
    }

    @Contract("_, _ -> new")
    public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> createBasedOnSupplier(@NotNull Supplier<Boolean> isFlatDatabase, Map<String, Object> settingsObjects) {
        if (isFlatDatabase.get()) {
            return new DatabaseFactory<>(new DatabaseSettings(DatabaseSettings.Type.FLAT, settingsObjects));
        }

        return new DatabaseFactory<>(new DatabaseSettings(DatabaseSettings.Type.SQL, settingsObjects));
    }
}
