package me.opkarol.oplibrary.injection.formatter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class NameBuilder {

    @Contract(pure = true)
    public static @NotNull String name(String input) {
        return "&e&l" + input;
    }
}
