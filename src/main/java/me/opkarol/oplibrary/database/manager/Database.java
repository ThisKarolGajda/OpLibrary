package me.opkarol.oplibrary.database.manager;

import me.opkarol.oplibrary.database.DatabaseEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Database<PK extends Serializable, T extends DatabaseEntity<PK>> {
    private final DatabaseHolder<PK, T> databaseHandler;

    public Database(Class<T> clazz, Class<T[]> clazzArray) {
        String lastPartOfClassName = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1).toLowerCase();
        this.databaseHandler = DatabaseFactory.createJSON(lastPartOfClassName + ".db", clazz, clazzArray, useMultiFilesForJSON());
    }

    public void save(T t) {
        databaseHandler.save(t);
    }

    public Optional<T> get(PK id) {
        return Optional.ofNullable(databaseHandler.getById(id));
    }

    public @Nullable T getUnsafe(PK id) {
        return databaseHandler.getById(id);
    }

    public void delete(PK id) {
        databaseHandler.delete(id);
    }

    public void delete(@NotNull T t) {
        delete(t.getId());
    }

    public List<T> getAll() {
        return databaseHandler.getAll();
    }

    public boolean contains(PK id) {
        return get(id).isPresent();
    }

    public boolean contains(@NotNull T t) {
        return contains(t.getId());
    }

    public boolean containsAll(@NotNull List<PK> ids) {
        return ids.stream()
                .allMatch(this::contains);
    }

    public boolean ifPresent(PK id, @NotNull Consumer<T> consumer) {
        return get(id).map(t -> {
            consumer.accept(t);
            return true;
        }).orElse(false);
    }

    public List<DatabaseSettings.Type> getTypes() {
        return List.of(DatabaseSettings.Type.JSON);
    }

    public boolean useMultiFilesForJSON() {
        return false;
    }

    public CompletableFuture<Void> saveAsync(T t) {
        return CompletableFuture.runAsync(() -> save(t));
    }

    public CompletableFuture<Void> deleteAsync(PK id) {
        return CompletableFuture.runAsync(() -> delete(id));
    }

    public CompletableFuture<Optional<T>> getByIdAsync(PK id) {
        return CompletableFuture.supplyAsync(() -> get(id));
    }
}
