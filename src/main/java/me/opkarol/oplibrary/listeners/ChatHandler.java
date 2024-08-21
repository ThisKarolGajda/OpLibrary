package me.opkarol.oplibrary.listeners;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@IgnoreInject
public class ChatHandler extends BasicListener {
    private static ChatHandler chatHandler;
    private final Map<UUID, Queue<Consumer<String>>> handlers = new HashMap<>();

    public ChatHandler() {
        chatHandler = this;
        runListener();
    }

    public static void add(UUID uuid, Consumer<String> consumer) {
        getInstance().addHandler(uuid, consumer);
    }

    public static void add(@NotNull HumanEntity humanEntity, Consumer<String> consumer) {
        getInstance().addHandler(humanEntity.getUniqueId(), consumer);
    }

    public static ChatHandler getInstance() {
        return chatHandler == null ? new ChatHandler() : chatHandler;
    }

    @EventHandler
    public void chatEvent(@NotNull AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Optional.ofNullable(handlers.get(uuid))
                .ifPresent(queue -> {
                    Consumer<String> consumer = queue.poll();
                    if (consumer != null) {
                        consumer.accept(event.getMessage());
                        event.setCancelled(true);
                    }
                });
    }

    public void addHandler(UUID uuid, Consumer<String> consumer) {
        Queue<Consumer<String>> queue = handlers.getOrDefault(uuid, new ArrayDeque<>());
        queue.add(consumer);
        handlers.put(uuid, queue);
    }

    public void addHandlerWithPlayerConsumer(UUID uuid, Consumer<String> consumer, @NotNull Consumer<Player> playerConsumer) {
        addHandler(uuid, consumer);
        playerConsumer.accept(Bukkit.getPlayer(uuid));
    }

    public void addHandlerWithPlayerSendMessage(UUID uuid, Consumer<String> consumer, String playerMessage) {
        addHandlerWithPlayerConsumer(uuid, consumer, player -> player.sendMessage(FormatTool.formatMessage(playerMessage)));
    }

    public void removeHandler(UUID uuid) {
        handlers.remove(uuid);
    }

    @SafeVarargs
    public final void addHandlers(UUID uuid, Consumer<String> @NotNull ... consumers) {
        for (Consumer<String> consumer : consumers) {
            addHandler(uuid, consumer);
        }
    }
}