package me.opkarol.oplibrary.database.manager;

import java.util.Map;

public record DatabaseSettings(DatabaseSettings.Type type,
                               Map<String, Object> objects) {

    public enum Type {
        JSON,
    }
}
