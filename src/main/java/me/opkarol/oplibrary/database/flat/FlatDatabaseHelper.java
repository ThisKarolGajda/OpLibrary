package me.opkarol.oplibrary.database.flat;

import me.opkarol.oplibrary.configurationfile.ConfigurationFile;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FlatDatabaseHelper<K> {
    private final ConfigurationFile configuration;

    public FlatDatabaseHelper(Plugin plugin, String fileName) {
        this.configuration = new ConfigurationFile(plugin, fileName, false);
        this.configuration.createNewEmptyFile();
    }

    public K loadObject() {
        File file = configuration.getFile();
        if (!file.isFile() || !file.exists() || file.length() == 0) {
            return null;
        }
        try {
            return readFile(configuration.getFile().getPath());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveObject(K objectToSave) {
        try {
            saveFile(objectToSave, configuration.getFile().getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile(K object, String path) throws IOException {
        if (object == null) {
            return;
        }
        try (ObjectOutputStream os = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)))) {
            os.writeObject(object);
        }
    }

    @Nullable
    private K readFile(String path) throws ClassNotFoundException, IOException {
        try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(Paths.get(path)))) {
            return (K) is.readObject();
        } catch (StreamCorruptedException ignore) {
            return null;
        }
    }
}