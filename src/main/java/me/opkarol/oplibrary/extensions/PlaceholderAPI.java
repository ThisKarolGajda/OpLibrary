package me.opkarol.oplibrary.extensions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
@IgnoreInject
public class PlaceholderAPI {
    private static PlaceholderAPI placeholderAPI;
    private final boolean enabled;

    public PlaceholderAPI() {
        placeholderAPI = this;
        enabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static String set(OfflinePlayer player, String string) {
        if (!getInstance().enabled) {
            return string;
        }

        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
    }

    public static PlaceholderAPI getInstance() {
        return placeholderAPI == null ? new PlaceholderAPI() : placeholderAPI;
    }

    public static @NotNull PlaceholderAPI registerExtension(String identifier, String author, String version, BiFunction<OfflinePlayer, String, String> biFunction) {
        PlaceholderAPI placeholderAPI = getInstance();
        if (placeholderAPI.isEnabled()) {
            new PlaceholderExpansion() {
                @Override
                public @NotNull String getIdentifier() {
                    return identifier;
                }

                @Override
                public @NotNull String getAuthor() {
                    return author;
                }

                @Override
                public @NotNull String getVersion() {
                    return version;
                }

                @Override
                public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
                    return biFunction.apply(player, params);
                }

                @Override
                public boolean persist() {
                    return true;
                }

                @Override
                public boolean canRegister() {
                    return true;
                }
            }.register();
        }
        return placeholderAPI;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
