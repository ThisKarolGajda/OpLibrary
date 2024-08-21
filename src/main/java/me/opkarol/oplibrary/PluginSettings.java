package me.opkarol.oplibrary;

import org.jetbrains.annotations.Nullable;

public interface PluginSettings {
    /**
     * If null, this method won't register BStats,
     * in other case it will register using given
     * service id.
     *
     * @return integer BStats service id
     */
    @Nullable
    Integer registerBStatsOnStartup();
}
