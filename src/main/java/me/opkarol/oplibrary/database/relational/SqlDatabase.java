package me.opkarol.oplibrary.database.relational;

import me.opkarol.oplibrary.database.manager.IDatabase;
import me.opkarol.oporm.AsyncOpOrm;
import me.opkarol.oporm.DatabaseEntity;
import me.opkarol.oporm.OpOrm;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlDatabase<T extends DatabaseEntity> implements IDatabase<T> {
    private final OpOrm orm;
    private final Class<T> clazz;
    private Map<Integer, T> cache = new HashMap<>();

    public SqlDatabase(String url, String host, String password, Class<T> clazz) {
        this.clazz = clazz;
        this.orm = new AsyncOpOrm(url, host, password);
    }

    @Override
    public void save(T t) {
        cache.put(t.getId(), t);
        orm.save(t);
    }

    @Override
    public void initialize() {
        orm.createTable(clazz);
        cache = convertListToMap(orm.findAll(clazz));
    }

    @Override
    public T getById(int id) {
        // Check if the entity is in the cache
        T cachedEntity = cache.get(id);
        if (cachedEntity != null) {
            return cachedEntity;
        }

        // If not in the cache, fetch it from the database
        T databaseEntity = orm.findById(clazz, id);
        if (databaseEntity != null) {
            // Update the cache
            cache.put(id, databaseEntity);
        }

        return databaseEntity;
    }

    @Override
    public void delete(int id) {
        cache.remove(id);
        orm.deleteById(clazz, id);
    }

    @Override
    public List<T> getAll() {
        return cache.values().stream().toList();
    }

    // Utility method to convert List<T> to Map<Integer, T>
    private @NotNull Map<Integer, T> convertListToMap(@NotNull List<T> list) {
        Map<Integer, T> resultMap = new HashMap<>();
        for (T item : list) {
            resultMap.put(item.getId(), item);
        }
        return resultMap;
    }

    @Override
    public void onDisable() {
        orm.closeConnection();
    }
}