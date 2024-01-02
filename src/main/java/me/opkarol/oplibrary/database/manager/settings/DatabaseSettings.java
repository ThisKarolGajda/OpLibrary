package me.opkarol.oplibrary.database.manager.settings;

import java.util.Map;

public record DatabaseSettings(me.opkarol.oplibrary.database.manager.settings.DatabaseSettings.Type type,
                               Map<String, Object> objects) {

    public enum Type {
        FLAT,
        SQL,
    }
}
