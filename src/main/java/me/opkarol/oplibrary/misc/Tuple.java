package me.opkarol.oplibrary.misc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public record Tuple<A, B>(A first, B second) implements Serializable {

    @Contract("_, _ -> new")
    public static <A, B> @NotNull Tuple<A, B> of(A a, B b) {
        return new Tuple<>(a, b);
    }

    public static <A, B> @NotNull Tuple<A, B> empty() {
        return new Tuple<>(null, null);
    }

    public boolean isEmpty() {
        return first == null && second == null;
    }
}
