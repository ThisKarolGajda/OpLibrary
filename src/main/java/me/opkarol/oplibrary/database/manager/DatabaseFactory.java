package me.opkarol.oplibrary.database.manager;

import me.opkarol.oplibrary.database.DatabaseEntity;
import me.opkarol.oplibrary.database.JSONDatabase;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public final class DatabaseFactory<PK extends Serializable, T extends DatabaseEntity<PK>> extends DatabaseHolder<PK, T> {

    /**
     * Objects:
     * SQL: url, host, password, class,
     * FLAT: plugin, fileName
     *
     * @param settings Database settings
     */
    public DatabaseFactory(@NotNull DatabaseSettings settings) {
        super(new JSONDatabase<>((String) settings.objects().get("fileName"), (Class<T>) settings.objects().get("class"), (Class<T[]>) settings.objects().get("class"), false));
    }

    public static <PK extends Serializable, T extends DatabaseEntity<PK>> @NotNull DatabaseHolder<PK, T> createJSON(String fileName, Class<T> clazz, Class<T[]> classArray, boolean useMultiFiles) {
        return new DatabaseHolder<>(new JSONDatabase<>(fileName, clazz, classArray, useMultiFiles));
    }

}
