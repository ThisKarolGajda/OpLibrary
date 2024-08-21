package me.opkarol.oplibrary.debug;

import me.opkarol.oplibrary.injection.config.Config;
import org.bukkit.plugin.Plugin;

public class PluginDebugger {
    private final Plugin plugin;
    @Config
    private static boolean debugEnabled = false;

    public PluginDebugger(Plugin plugin) {
        this.plugin = plugin;
    }

    public void debug(String message) {
        if (debugEnabled) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
}
