package me.opkarol.oplibrary.injection.inventories;

import java.util.Map;

public class ArgsMap {
    private final Map<String, Object> args;

    public ArgsMap(Map<String, Object> args) {
        this.args = args;
    }

    public <K> K getArg(String key) {
        return (K) args.get(key);
    }
}
