package me.opkarol.oplibrary.database.manager;

import java.io.Serializable;
import java.util.List;

public interface IDatabase<PK extends Serializable, T> {

    void save(T t);

    void initialize();

    T getById(PK id);

    void delete(PK id);

    List<T> getAll();

    default void onDisable() {

    }
}
