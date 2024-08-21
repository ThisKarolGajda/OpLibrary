package me.opkarol.oplibrary.util;

import me.opkarol.oplibrary.debug.PluginDebugger;
import me.opkarol.oplibrary.injection.DependencyInjection;

public class Helper {

    public void debug(String message) {
        DependencyInjection.get(PluginDebugger.class).debug(message);
    }
}
