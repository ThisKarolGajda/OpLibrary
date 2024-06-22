package me.opkarol.oplibrary.database;

import me.opkarol.oplibrary.database.manager.Database;
import me.opkarol.oporm.DatabaseEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DatabaseTypesCache {
    private final Map<Object, Class<?>> map = new HashMap<>();

    public <PK extends Serializable, T extends DatabaseEntity<PK>, O extends Database<PK, T>> void addType(T type, Class<O> clazz) {
        map.put(type, clazz);
    }

    public <PK extends Serializable, T extends DatabaseEntity<PK>, O extends Database<PK, T>> Class<O> getType(Object type) {
        return (Class<O>) map.get(type);
    }
}
