package me.opkarol.oplibrary.injection.config;

import me.opkarol.oplibrary.debug.PluginDebugger;
import me.opkarol.oplibrary.injection.DependencyInjection;
import me.opkarol.oplibrary.injection.Inject;
import me.opkarol.oplibrary.util.ClassFinder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public class ConfigInjectionManager {
    @Inject
    private static ConfigManager configManager;

    @Inject
    public ConfigInjectionManager() {
        autoInject();
    }

    public static void autoInject() {
        Set<Class<?>> set = DependencyInjection.get(ClassFinder.class).findAllClassesUsingClassLoader();
        PluginDebugger debugger = DependencyInjection.get(PluginDebugger.class);

        for (Class<?> clazz : set) {
            debugger.debug("Found Class: " + clazz.getName());
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if ((field.isAnnotationPresent(Config.class) || field.getName().startsWith("cfg_")) && Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Config config = field.getAnnotation(Config.class);
                    Config classConfig = clazz.getAnnotation(Config.class);

                    String keyStart = (classConfig != null && !classConfig.path().isEmpty()) ? classConfig.path() : config.path();
                    try {
                        Object object = field.get(null);
                        debugger.debug("Found default object: " + object);
                        String key = keyStart + (!keyStart.isEmpty() ? "." : "") + field.getName();
                        Object loadedObject = configManager.get(key, object);
                        if (loadedObject == null) {
                            loadedObject = object;
                        }

                        configManager.set(key, loadedObject);
                        debugger.debug("Set default message in config.yml for key: " + key);

                        field.set(null, loadedObject);
                        debugger.debug("Injected value into field: " + field.getName() + " of class: " + clazz.getName());
                    } catch (IllegalAccessException exception) {
                        debugger.debug("Failed to inject value into field: " + field.getName() + " of class: " + clazz.getName());
                    }
                }
            }
        }
    }


}