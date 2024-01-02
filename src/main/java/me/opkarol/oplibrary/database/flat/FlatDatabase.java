package me.opkarol.oplibrary.database.flat;

import me.opkarol.oplibrary.database.manager.IDatabase;
import me.opkarol.oporm.DatabaseEntity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatDatabase<T extends DatabaseEntity> implements IDatabase<T> {
    private final FlatDatabaseHelper<Map<Integer, T>> helper;
    private Map<Integer, T> cache = new HashMap<>();

    public FlatDatabase(Plugin plugin, String fileName) {
        this.helper = new FlatDatabaseHelper<>(plugin, fileName);
    }

    @Override
    public void save(T t) {
        cache.put(t.getId(), t);
        helper.saveObject(cache);
    }

    @Override
    public void initialize() {
        cache = helper.loadObject();
        if (cache == null) {
            cache = new HashMap<>();
        }
    }

    @Override
    public T getById(int id) {
        return cache.get(id);
    }

    @Override
    public void delete(int id) {
        cache.remove(id);
        helper.saveObject(cache);
    }

    @Override
    public List<T> getAll() {
        return cache.values().stream().toList();
    }
}
