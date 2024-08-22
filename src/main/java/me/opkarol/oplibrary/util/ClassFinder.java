package me.opkarol.oplibrary.util;

import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

public class ClassFinder extends Helper {
    private static Set<Class<?>> cachedClasses = null;
    private final Plugin plugin;

    public ClassFinder(Plugin plugin) {
        this.plugin = plugin;
    }

    public Set<Class<?>> findAllClassesUsingClassLoader() {
        if (cachedClasses != null) {
            return cachedClasses;
        }

        cachedClasses = new HashSet<>();

        // Get GlobalHelper classes
        findClassesRecursively("me.opkarol.oplibrary", cachedClasses);

        // Get plugin classes
        String packageName = plugin.getClass().getPackage().getName().replace("/", ".");
        findClassesRecursively(packageName, cachedClasses);
        return cachedClasses;
    }

    private void findClassesRecursively(@NotNull String packageName, Set<Class<?>> classes) {
        debug("Package: " + packageName);
        URL packageURL = plugin.getClass().getClassLoader().getResource(packageName.replace('.', '/'));

        if (packageURL == null) {
            debug("Package URL is null for package: " + packageName);
            return;
        }

        String protocol = packageURL.getProtocol();
        if ("file".equals(protocol)) {
            loadClassesFromDirectory(packageURL, packageName, classes);
        } else if ("jar".equals(protocol)) {
            loadClassesFromJar(packageURL, packageName, classes);
        } else {
            debug("Unsupported protocol: " + protocol);
        }
    }

    private void loadClassesFromDirectory(@NotNull URL packageURL, String packageName, Set<Class<?>> classes) {
        try {
            File directory = new File(packageURL.toURI());
            if (!directory.exists()) {
                debug("Directory does not exist for package: " + packageName);
                return;
            }

            debug("Searching for classes in directory: " + directory.getAbsolutePath());
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        debug("Found directory: " + file.getName());
                        findClassesRecursively(packageName + "." + file.getName(), classes);
                    } else if (file.getName().endsWith(".class")) {
                        addClassToSet(packageName, file, classes);
                    }
                }
            } else {
                debug("No files found in directory: " + directory.getAbsolutePath());
            }
        } catch (URISyntaxException e) {
            debug("Error while accessing package: " + packageName);
        }
    }

    private void loadClassesFromJar(@NotNull URL packageURL, String packageName, Set<Class<?>> classes) {
        String jarPath = packageURL.getPath().substring(5, packageURL.getPath().indexOf("!"));
        try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
            jarFile.stream()
                    .filter(entry -> entry.getName().startsWith(packageName.replace('.', '/') + "/") && entry.getName().endsWith(".class"))
                    .forEach(entry -> addClassToSet(entry.getName(), classes));
        } catch (Exception ignored) {
            debug("Failed to read JAR file: " + jarPath);
        }
    }

    private void addClassToSet(String packageName, @NotNull File file, Set<Class<?>> classes) {
        String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
        add(classes, className);
    }

    private void addClassToSet(@NotNull String entryName, Set<Class<?>> classes) {
        String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
        add(classes, className);
    }

    private void add(Set<Class<?>> classes, String className) {
        if (className.startsWith("me.opkarol.oplibrary.extensions")) {
            return;
        }

        try {
            debug("Adding class: " + className);
            Class<?> clazz = Class.forName(className);
            if (!clazz.isAnnotationPresent(IgnoreInject.class) && !clazz.isInterface()) {
                classes.add(clazz);
                debug("Loaded class: " + className);
            } else {
                debug("Skipping class: " + className);
            }
        } catch (Exception ignored) {
            debug("Class not found: " + className);
        }
    }
}