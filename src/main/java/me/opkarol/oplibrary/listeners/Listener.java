package me.opkarol.oplibrary.listeners;

import me.opkarol.oplibrary.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.io.Serializable;

public class Listener implements IListener, org.bukkit.event.Listener, Serializable {

    public Listener() {
        runListener();
    }

    @Override
    public void runListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Plugin.getInstance());
    }

    @Override
    public void stopListener() {
        HandlerList.unregisterAll(this);
    }
}