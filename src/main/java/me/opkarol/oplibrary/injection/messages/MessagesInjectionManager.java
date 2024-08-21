package me.opkarol.oplibrary.injection.messages;

import me.opkarol.oplibrary.debug.PluginDebugger;
import me.opkarol.oplibrary.injection.DependencyInjection;
import me.opkarol.oplibrary.injection.Inject;
import me.opkarol.oplibrary.injection.formatter.DefaultTextFormatter;
import me.opkarol.oplibrary.util.ClassFinder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public class MessagesInjectionManager {
    @Inject
    private static MessagesManager messagesManager;
    @Inject
    private static DefaultTextFormatter textFormatter;

    @Inject
    public MessagesInjectionManager() {
        autoInject();
    }

    public static void autoInject() {
        Set<Class<?>> set = DependencyInjection.get(ClassFinder.class).findAllClassesUsingClassLoader();
        PluginDebugger debugger = DependencyInjection.get(PluginDebugger.class);

        for (Class<?> clazz : set) {
            debugger.debug("Found Class: " + clazz.getName());
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if ((field.isAnnotationPresent(Message.class) || field.getName().startsWith("msg_") || field.getType().equals(StringMessage.class)) && Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        Object defaultObject = field.get(null);
                        if (defaultObject instanceof String string) {
                            debugger.debug("Found default translation String: " + string);
                            String key = field.getName();
                            String loadedMessage = messagesManager.getMessage(key, textFormatter.formatMessage(string));
                            messagesManager.setMessage(key, loadedMessage);
                            debugger.debug("Set default message in messages.yml for key: " + key);
                            field.set(null, loadedMessage);
                            debugger.debug("Injected value into field: " + field.getName() + " of class: " + clazz.getName());
                        } else if (defaultObject instanceof StringMessage stringMessage) {
                            debugger.debug("Found default translation StringMessage: " + stringMessage);
                            String key = field.getName();
                            String loadedMessage = messagesManager.getMessage(key, textFormatter.formatMessage(stringMessage.defaultMessage()));
                            messagesManager.setMessage(key, loadedMessage);
                            debugger.debug("Set default message in messages.yml for key: " + key);
                            stringMessage.setObject(loadedMessage);
                            field.set(null, stringMessage);
                            debugger.debug("Injected value into field: " + field.getName() + " of class: " + clazz.getName());
                        }
                    } catch (IllegalAccessException exception) {
                        debugger.debug("Failed to inject value into field: " + field.getName() + " of class: " + clazz.getName());
                    }
                }
            }
        }
    }
}