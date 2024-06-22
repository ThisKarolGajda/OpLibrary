package me.opkarol.oplibrary.database.manager;

import me.opkarol.oplibrary.autostart.IDisable;
import me.opkarol.oplibrary.autostart.OpAutoDisable;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractDatabase<PK extends Serializable, T> implements IDisable {

    public AbstractDatabase() {
        OpAutoDisable.add(this);
    }

    public abstract void save(T t);

    public abstract void initialize();

    public abstract T getById(PK id);

    public abstract void delete(PK id);

    public abstract List<T> getAll();

    @Override
    public abstract void onDisable();
}
