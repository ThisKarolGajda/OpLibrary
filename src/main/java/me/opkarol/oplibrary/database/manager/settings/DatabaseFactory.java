package me.opkarol.oplibrary.database.manager.settings;

import me.opkarol.oplibrary.database.flat.FlatDatabase;
import me.opkarol.oplibrary.database.manager.DatabaseImpl;
import me.opkarol.oplibrary.database.relational.SqlDatabase;
import me.opkarol.oporm.DatabaseEntity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class DatabaseFactory<T extends DatabaseEntity> extends DatabaseImpl<T> {

    public DatabaseFactory(@NotNull DatabaseSettings settings) {
        super(settings.type() == DatabaseSettings.Type.SQL
                ? new SqlDatabase<>(settings.objects().get("url").toString(), settings.objects().get("host").toString(), settings.objects().get("password").toString(), (Class<T>) settings.objects().get("class"))
                : new FlatDatabase<>((Plugin) settings.objects().get("plugin"), (String) settings.objects().get("fileName")));
    }

    public static <T extends DatabaseEntity> @NotNull DatabaseImpl<T> createFlat(Plugin plugin, String fileName) {
        return new DatabaseImpl<>(new FlatDatabase<>(plugin, fileName));
    }

    public static <T extends DatabaseEntity> @NotNull DatabaseImpl<T> createSql(String url, String host, String password, Class<T> clazz) {
        return new DatabaseImpl<>(new SqlDatabase<>(url, host, password, clazz));
    }

    @Contract("_, _ -> new")
    public static <T extends DatabaseEntity> @NotNull DatabaseImpl<T> createBasedOnSupplier(@NotNull Supplier<Boolean> flatDatabaseSupplier, Map<String, Object> settingsObjects) {
        if (flatDatabaseSupplier.get()) {
            return new DatabaseFactory<>(new DatabaseSettings(DatabaseSettings.Type.FLAT, settingsObjects));
        }
        return new DatabaseFactory<>(new DatabaseSettings(DatabaseSettings.Type.SQL, settingsObjects));
    }
}
