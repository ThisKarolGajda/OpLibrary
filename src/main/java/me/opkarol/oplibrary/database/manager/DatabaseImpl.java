package me.opkarol.oplibrary.database.manager;

import me.opkarol.oporm.DatabaseEntity;

import java.io.Serializable;
import java.util.List;

public class DatabaseImpl<PK extends Serializable, T extends DatabaseEntity<PK>> {
    private final IDatabase<PK, T> database;

    public DatabaseImpl(IDatabase<PK, T> database) {
        this.database = database;
        initialize();
    }

    public void save(T t) {
        database.save(t);
    }

    private void initialize() {
        database.initialize();
    }

    public T getById(PK id) {
        return database.getById(id);
    }

    public void delete(PK id) {
        database.delete(id);
    }

    public List<T> getAll() {
        return database.getAll();
    }

    public void onDisable() {
        database.onDisable();
    }
}
