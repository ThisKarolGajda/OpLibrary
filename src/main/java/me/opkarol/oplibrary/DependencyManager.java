package me.opkarol.oplibrary;

import java.util.HashMap;
import java.util.Map;

public class DependencyManager {
    private final Map<Class<?>, Object> map = new HashMap<>();

    public <T> void register(Class<T> clazz, T t) {
        map.put(clazz, t);
    }

    public <T> T get(Class<T> clazz) {
        return (T) map.get(clazz);
    }

    public void dispose() {
        map.clear();
    }
}
