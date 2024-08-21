package me.opkarol.oplibrary.listeners;

import me.opkarol.oplibrary.Plugin;
import me.opkarol.oplibrary.injection.IgnoreInject;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.Serializable;

@IgnoreInject
public class BasicListener implements IListener, Listener, Serializable {
    @Override
    public void runListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Plugin.getInstance());
    }

    @Override
    public void stopListener() {
        HandlerList.unregisterAll(this);
    }
}