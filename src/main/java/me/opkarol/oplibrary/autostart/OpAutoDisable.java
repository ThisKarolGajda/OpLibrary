package me.opkarol.oplibrary.autostart;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class OpAutoDisable {
    private static final List<IDisable> tasks = new ArrayList<>();
    private static OpAutoDisable autoStart;

    public OpAutoDisable() {
        autoStart = this;
    }

    public static OpAutoDisable getInstance() {
        return autoStart == null ? new OpAutoDisable() : autoStart;
    }

    public static void add(IDisable runnable) {
        tasks.add(runnable);
    }

    public static void registerDisable() {
        for (IDisable iDisable : tasks) {
            iDisable.onDisable();
        }
    }
}
