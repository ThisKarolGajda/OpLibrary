package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.runnable.OpRunnable;
import me.opkarol.oplibrary.tools.OpComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unsued")
@IgnoreInject
@SerializableAs("OpActionBar")
public class OpActionBar implements Serializable, ConfigurationSerializable {
    private String text;
    private transient OpComponent actionBar;
    private transient List<Player> receivers;
    private transient OpRunnable runnable;

    public OpActionBar(OpComponent actionBar) {
        this.actionBar = actionBar;
    }

    public OpActionBar(String text) {
        this.text = text;
    }

    public OpActionBar() {
        this.text = null;
    }

    public OpActionBar send() {
        receivers.forEach(this::send);
        return this;
    }

    public OpActionBar send(@NotNull Player player) {
        return send(player, actionBar);
    }

    public OpActionBar send(Player player, OpComponent component) {
        if (actionBar == null) {
            build();
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component.build());
        return this;
    }

    public OpActionBar send(Player player, String text) {
        return send(player, new OpComponent(text));
    }

    public OpComponent getActionBar() {
        return actionBar;
    }

    public OpActionBar setActionBar(OpComponent actionBar) {
        this.actionBar = actionBar;
        return this;
    }

    public List<Player> getReceivers() {
        return receivers;
    }

    public OpActionBar setReceivers(List<Player> receivers) {
        this.receivers = receivers;
        return this;
    }

    public OpActionBar addReceiver(Player player) {
        List<Player> list = getReceivers();
        if (getReceivers() == null) {
            list = new ArrayList<>();
        }
        list.add(player);
        setReceivers(list);
        return this;
    }

    public OpActionBar sendLooped(int times) {
        final int[] i = {times};
        runnable = new OpRunnable(r -> {
            if (i[0] < 1) {
                sendEmpty();
                r.cancelTask();
            } else {
                build(text.replace("%time%", String.valueOf(i[0]))).send();
            }
            i[0]--;
        }).runTaskTimerAsynchronously(0, 20);
        return this;
    }

    public OpActionBar sendEmpty() {
        receivers.forEach(player -> send(player, new OpComponent("&l")));
        return this;
    }

    public void cancel() {
        runnable.cancelTask();
    }

    public String getText() {
        return text;
    }

    public OpActionBar setText(String text) {
        this.text = text;
        return this;
    }

    public OpActionBar build(String text) {
        actionBar = new OpComponent(text);
        return this;
    }

    public OpActionBar build() {
        return build(text);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return map;
    }

    public static @NotNull OpActionBar deserialize(@NotNull Map<String, Object> map) {
        String text = (String) map.get("text");
        return new OpActionBar(text);
    }
}
