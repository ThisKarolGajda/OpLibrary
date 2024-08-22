package me.opkarol.oplibrary.injection.messages;

import me.opkarol.oplibrary.injection.inventories.items.ItemClick;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class StringMessage {
    private final Function<Player, Map<String, String>> playerFormatterFunction;
    private final Function<Object, Map<String, String>> formatterFunction;
    private final String defaultMessage;
    private String object;

    public StringMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
        this.playerFormatterFunction = null;
        this.formatterFunction = null;
    }

    public StringMessage(String defaultMessage, Function<Object, Map<String, String>> formatterFunction, Function<Player, Map<String, String>> playerFormatterFunction) {
        this.defaultMessage = defaultMessage;
        this.formatterFunction = formatterFunction;
        this.playerFormatterFunction = playerFormatterFunction;
    }

    public static StringMessage player(String defaultMessage, Function<Player, Map<String, String>> playerFormatterFunction) {
        return new StringMessage(defaultMessage, null, playerFormatterFunction);
    }

    public static StringMessage arg(String defaultMessage, Function<Object, Map<String, String>> formatterFunction) {
        return new StringMessage(defaultMessage, formatterFunction, null);
    }

    public String get() {
        return object == null ? defaultMessage : object;
    }

    public void send(@NotNull Player player) {
        if (playerFormatterFunction != null) {
            send(player, playerFormatterFunction.apply(player));
        } else {
            player.sendMessage(getFormatted());
        }
    }

    public void send(@NotNull Player player, Player target) {
        if (playerFormatterFunction != null) {
            send(player, playerFormatterFunction.apply(target));
        } else {
            player.sendMessage(getFormatted());
        }
    }

    public void send(Player player, @NotNull Object arg) {
        if (formatterFunction != null) {
            send(player, formatterFunction.apply(arg));
        } else {
            player.sendMessage(getFormatted());
        }
    }

    public void send(@NotNull ItemClick click) {
        send(click.getPlayer());
    }

    public void sendGlobal(@NotNull Map<String, String> replacements) {
        Bukkit.broadcastMessage(FormatTool.formatMessage(getString(replacements)));
    }

    @NotNull
    public String getString(@NotNull Map<String, String> replacements) {
        StringBuilder formattedMessage = new StringBuilder(get());
        replacements.forEach((replace, replacement) -> {
            int index;
            while ((index = formattedMessage.indexOf(replace)) != -1) {
                formattedMessage.replace(index, index + replace.length(), replacement);
            }
        });

        return formattedMessage.toString();
    }

    public void send(@NotNull Player player, @NotNull Map<String, String> replacements) {
        player.sendMessage(FormatTool.formatMessage(getString(replacements)));
    }

    public String defaultMessage() {
        return defaultMessage;
    }

    public void sendGlobal() {
        Bukkit.broadcastMessage(getFormatted());
    }

    public @NotNull String getFormatted() {
        return FormatTool.formatMessage(get());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StringMessage) obj;
        return Objects.equals(this.defaultMessage, that.defaultMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultMessage);
    }

    @Override
    public String toString() {
        return "StringMessage{" +
                "defaultMessage='" + defaultMessage + '\'' +
                ", playerFormatterFunction=" + playerFormatterFunction +
                ", formatterFunction=" + formatterFunction +
                ", object='" + object + '\'' +
                '}';
    }

    void setObject(String object) {
        this.object = object;
    }
}
