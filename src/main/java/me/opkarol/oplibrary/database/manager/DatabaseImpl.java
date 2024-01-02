package me.opkarol.oplibrary.database.manager;

import me.opkarol.oporm.DatabaseEntity;

import java.util.List;

public class DatabaseImpl<T extends DatabaseEntity> {
    private final IDatabase<T> database;

    public DatabaseImpl(IDatabase<T> database) {
        this.database = database;
        initialize();
    }

    public void save(T t) {
        database.save(t);
    }

    private void initialize() {
        database.initialize();
    }

    public T getById(int id) {
        return database.getById(id);
    }

    public void delete(int id) {
        database.delete(id);
    }

    public List<T> getAll() {
        return database.getAll();
    }

    public void onDisable() {
        database.onDisable();
    }
}
