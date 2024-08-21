package me.opkarol.oplibrary.injection.wrapper;

import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("ColorWrapper")
public record ColorWrapper(Color color) implements ConfigurationSerializable {

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("color", String.format("#%06X", (0xFFFFFF & color.asRGB())));
    }

    public static ColorWrapper deserialize(Map<String, Object> map) {
        String hexColor = (String) map.get("color");
        return new ColorWrapper(Color.fromRGB(Integer.decode(hexColor)));
    }

    public @NotNull String toCode() {
        return String.format("#<%06X>", (0xFFFFFF & color.asRGB()));
    }
}