package me.opkarol.oplibrary.database.manager;

import java.util.List;

public interface IDatabase<T> {

    void save(T t);

    void initialize();

    T getById(int id);

    void delete(int id);

    List<T> getAll();

    default void onDisable() {

    }
}
