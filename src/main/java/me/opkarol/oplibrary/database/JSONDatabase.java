package me.opkarol.oplibrary.database;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.configurationfile.ConfigurationFile;
import me.opkarol.oplibrary.database.manager.AbstractDatabase;
import me.opkarol.oporm.DatabaseEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.opkarol.oplibrary.gson.GsonBuilder.gson;

@SuppressWarnings("all")
public class JSONDatabase<PK extends Serializable, T extends DatabaseEntity<PK>> extends AbstractDatabase<PK, T> {
    private final ConfigurationFile file;
    private final Class<T[]> classArray;
    private final Class<T> clazz;
    private final String fileName;
    private Map<PK, T> cache;

    public JSONDatabase(String fileName, Class<T> clazz, Class<T[]> classArray, boolean useMultiFiles) {
        this.classArray = classArray;
        this.fileName = fileName;
        this.clazz = clazz;
        if (!useMultiFiles) {
            this.file = new ConfigurationFile(Plugin.getInstance(), fileName, false);
            this.file.createNewEmptyFile();
        } else {
            this.file = null;
        }
    }

    @Override
    public void save(T t) {
        cache.put(t.getId(), t);
        saveToFile(t);
    }

    @Override
    public void initialize() {
        this.cache = new HashMap<>();
        initializeCache();
    }

    @Override
    public T getById(PK id) {
        return cache.get(id);
    }

    @Override
    public void delete(PK id) {
        cache.remove(id);
        deleteFromFile(id);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void onDisable() {

    }

    private void initializeCache() {
        if (usesMultiFile()) {
            String directoryPath = "/" + fileName + "s";
            File directory = new File(Plugin.getInstance().getDataFolder(), directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                directory.mkdirs();
            }

            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try (Reader reader = new FileReader(file)) {
                            T t = gson.fromJson(reader, clazz);
                            if (t == null) {
                                continue;
                            }

                            cache.put(t.getId(), t);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            try (Reader reader = new FileReader(file.getFile())) {
                T[] array = gson.fromJson(reader, classArray);
                if (array == null || array.length == 0) {
                    return;
                }

                for (T t : array) {
                    cache.put(t.getId(), t);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile(T t) {
        if (usesMultiFile()) {
            String directoryPath = "/" + fileName + "s";
            File directory = new File(Plugin.getInstance().getDataFolder(), directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                directory.mkdirs();
            }

            File entityFile = new File(directory, t.getId() + ".json");
            try (Writer writer = new FileWriter(entityFile)) {
                gson.toJson(t, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (Writer writer = new FileWriter(file.getFile())) {
                gson.toJson(cache.values(), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFromFile(PK id) {
        if (usesMultiFile()) {
            String directoryPath = "/" + fileName + "s";
            File directory = new File(Plugin.getInstance().getDataFolder(), directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                return;
            }

            File entityFile = new File(directory, id + ".json");
            entityFile.delete();
        } else {
            try (Writer writer = new FileWriter(file.getFile())) {
                gson.toJson(cache.values(), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean usesMultiFile() {
        return file == null;
    }
}
