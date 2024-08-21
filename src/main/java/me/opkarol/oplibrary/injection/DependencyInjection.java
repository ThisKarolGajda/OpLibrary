package me.opkarol.oplibrary.injection;

import me.opkarol.oplibrary.debug.PluginDebugger;
import me.opkarol.oplibrary.util.ClassFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@IgnoreInject
public class DependencyInjection {
    private static final Map<Class<?>, Object> container = new ConcurrentHashMap<>();

    public static <C> void register(C instance) {
        container.put(instance.getClass(), instance);
    }

    public static <C> void registerInject(C instance) {
        container.put(instance.getClass(), instance);
        autoInject(instance.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <C> C get(Class<C> clazz) {
        C object = (C) container.get(clazz);
        if (object == null) {
            try {
                object = clazz.getDeclaredConstructor().newInstance();
                register(object);
                return object;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException ignore) {
            }
        }

        return object;
    }

    static void inject(@NotNull Class<?> clazz, @Nullable Class<?> injecting) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (injecting != null && !field.getType().equals(injecting)) {
                continue;
            }

            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    Object dependency = get(field.getType());
                    if (dependency != null) {
                        field.set(null, dependency);
                    } else {
                        DependencyInjection.get(PluginDebugger.class).debug("No dependency found for: " + field.getType().getName());
                    }
                } catch (IllegalAccessException e) {
                    DependencyInjection.get(PluginDebugger.class).debug("Failed to inject dependency into: " + field.getName());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static void autoInject() {
        Set<Class<?>> classes = get(ClassFinder.class).findAllClassesUsingClassLoader();
        for (Class<?> clazz : classes) {
            inject(clazz, null);
        }
    }

    public static void autoInject(Class<?> injecting) {
        Set<Class<?>> classes = get(ClassFinder.class).findAllClassesUsingClassLoader();
        for (Class<?> clazz : classes) {
            inject(clazz, injecting);
        }
    }

    public static void initializeConstructors() {
        Set<Class<?>> classes = get(ClassFinder.class).findAllClassesUsingClassLoader();
        for (Class<?> clazz : classes) {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                DependencyInjection.get(PluginDebugger.class).debug("Trying to invoke: " + constructor.getName());
                if (constructor.isAnnotationPresent(Inject.class)) {
                    DependencyInjection.get(PluginDebugger.class).debug("Constructor: " + constructor.getName() + " is annotated with @Inject");
                    if (constructor.getParameterTypes().length == 0) {
                        DependencyInjection.get(PluginDebugger.class).debug("Constructor: " + constructor.getName() + " has no parameters");
                        try {
                            constructor.newInstance();
                        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                            DependencyInjection.get(PluginDebugger.class).debug("Failed to inject dependency into: " + constructor.getName());
                        }
                    }
                }
            }
        }
    }

    public static void dispose() {
        container.clear();
    }
}