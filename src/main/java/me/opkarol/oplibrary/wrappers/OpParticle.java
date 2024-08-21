package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.location.OpLocation;
import me.opkarol.oplibrary.misc.StringUtil;
import me.opkarol.oplibrary.runnable.OpRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IgnoreInject
@SerializableAs("OpParticle")
public class OpParticle implements Serializable, ConfigurationSerializable {
    private float offsetX, offsetY, offsetZ;
    private int amount;
    private Particle particle;
    private transient OpLocation location;
    private transient List<Player> receivers;
    private transient OpRunnable animatedTask;

    public OpParticle(Particle particle) {
        this.particle = particle;
    }

    public OpParticle(Particle particle, int amount) {
        this.particle = particle;
        this.amount = amount;
    }

    public OpParticle(Particle particle, float offsetX, float offsetY, float offsetZ, int amount) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.amount = amount;
        this.particle = particle;
    }

    public OpParticle() {
    }

    public OpParticle setOffset(float offsetX, float offsetY, float offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        return this;
    }

    public String getOffset() {
        return String.format("%s;%s;%s", offsetX, offsetY, offsetZ);
    }

    public OpParticle setOffset(String offset) {
        if (offset != null) {
            String[] offsets = offset.split(";");
            if (offsets.length == 3) {
                this.offsetX = StringUtil.getFloat(offsets[0]);
                this.offsetY = StringUtil.getFloat(offsets[1]);
                this.offsetZ = StringUtil.getFloat(offsets[2]);
            }
        }
        return this;
    }

    public OpParticle setOffset(@NotNull Vector offset) {
        this.offsetX = (float) offset.getX();
        this.offsetY = (float) offset.getY();
        this.offsetZ = (float) offset.getZ();
        return this;
    }

    public Particle getParticle() {
        return particle;
    }

    public OpParticle setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public OpParticle setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public OpParticle setOffsetX(float offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public OpParticle setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public OpParticle setOffsetZ(float offsetZ) {
        this.offsetZ = offsetZ;
        return this;
    }

    public OpLocation getLocation() {
        return location;
    }

    public OpParticle setLocation(OpLocation opLocation) {
        this.location = opLocation;
        return this;
    }

    public OpParticle setLocation(Location location) {
        this.location = new OpLocation(location);
        return this;
    }

    public List<Player> getReceivers() {
        return receivers;
    }

    public OpParticle setReceivers(List<Player> receivers) {
        this.receivers = receivers;
        return this;
    }

    public OpParticle addReceiver(Player player) {
        List<Player> list = receivers == null ? new ArrayList<>() : receivers;
        list.add(player);
        return setReceivers(list);
    }

    public OpParticle display() {
        return display(getReceivers());
    }

    public <T> OpParticle display(T specialData) {
        return display(getReceivers(), specialData);
    }

    public <T> OpParticle display(List<Player> players, T specialData) {
        if (players == null) {
            return this;
        }

        if (location != null) {
            players.forEach(player -> player.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), amount, offsetX, offsetY, offsetZ, specialData));
        }

        return this;
    }

    public OpParticle display(Player player) {
        if (player == null) {
            return this;
        }

        if (location != null && particle != null) {
            player.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), amount, offsetX, offsetY, offsetZ);
        }

        return this;
    }

    public OpParticle displayForAllOnline() {
        setReceivers(new ArrayList<>(Bukkit.getOnlinePlayers()));
        return display();
    }

    public OpParticle display(List<Player> players) {
        if (players == null) {
            return this;
        }

        players.forEach(this::display);
        return this;
    }

    public OpParticle animateDisplay(int amount, int delay) {
        final int[] i = {0};
        animatedTask = new OpRunnable(r -> {
            if (i[0] >= amount) {
                r.cancelTask();
            }

            display();
            i[0]++;
        }).runTaskTimerAsynchronously(0, delay);
        return this;
    }

    public OpParticle displayByNearPlayers(Player byPlayer, int reach) {
        if (byPlayer == null) {
            return this;
        }
        List<Player> players = new ArrayList<>(List.of(byPlayer));
        for (Entity entity : byPlayer.getNearbyEntities(reach, reach, reach)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return display(players);
    }

    public void cancelAnimatedTask() {
        animatedTask.cancelTask();
    }

    @Override
    public String toString() {
        return "OpParticle{" +
                "offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", offsetZ=" + offsetZ +
                ", amount=" + amount +
                ", particle=" + particle +
                ", location=" + location +
                ", receivers=" + receivers +
                ", animatedTask=" + animatedTask +
                '}';
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("offsetX", offsetX);
        map.put("offsetY", offsetY);
        map.put("offsetZ", offsetZ);
        map.put("amount", amount);
        map.put("particle", particle.name());
        return map;
    }

    public static @NotNull OpParticle deserialize(@NotNull Map<String, Object> map) {
        OpParticle opParticle = new OpParticle();
        opParticle.setOffsetX(((Number) map.get("offsetX")).floatValue());
        opParticle.setOffsetY(((Number) map.get("offsetY")).floatValue());
        opParticle.setOffsetZ(((Number) map.get("offsetZ")).floatValue());
        opParticle.setAmount(((Number) map.get("amount")).intValue());
        opParticle.setParticle(Particle.valueOf((String) map.get("particle")));
        return opParticle;
    }
}