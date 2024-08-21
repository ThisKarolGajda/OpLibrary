package me.opkarol.oplibrary.location;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.misc.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
@IgnoreInject
@SerializableAs("OpLocation")
public class OpLocation implements Serializable, ConfigurationSerializable {
    private double x, y, z;
    private float pitch, yaw;
    private String world;
    private UUID worldUUID;

    public OpLocation(double x, double y, double z, float pitch, float yaw, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        if (world != null) {
            this.world = world.getName();
            this.worldUUID = world.getUID();
        }
    }

    public OpLocation(@NotNull Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
        World world1 = location.getWorld();
        if (world1 == null) {
            return;
        }
        this.world = world1.getName();
        this.worldUUID = world1.getUID();
    }

    public OpLocation(String string) {
        if (string != null && !string.isEmpty()) {
            String[] params = string.split(";");
            if (params.length == 6) {
                x = StringUtil.getDouble(params[0]);
                y = StringUtil.getDouble(params[1]);
                z = StringUtil.getDouble(params[2]);
                pitch = StringUtil.getFloat(params[3]);
                yaw = StringUtil.getFloat(params[4]);
                world = params[5];
                return;
            }
        }

        x = 0;
        y = 0;
        z = 0;
        pitch = 0;
        yaw = 0;
        world = null;
    }

    public OpLocation() {

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getBlockX() {
        return Math.toIntExact(Math.round(getX()));
    }

    public String getStringX() {
        return String.valueOf(getX());
    }

    public String getShortX() {
        return String.valueOf((int) getX());
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getBlockY() {
        return Math.toIntExact(Math.round(getY()));
    }

    public String getStringY() {
        return String.valueOf(getY());
    }

    public String getShortY() {
        return String.valueOf((int) getY());
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getBlockZ() {
        return Math.toIntExact(Math.round(getZ()));
    }

    public String getStringZ() {
        return String.valueOf(getZ());
    }

    public String getShortZ() {
        return String.valueOf((int) getZ());
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String getStringPitch() {
        return String.valueOf(getPitch());
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public String getStringYaw() {
        return String.valueOf(getYaw());
    }

    public World getWorld() {
        if (worldUUID != null) {
            return Bukkit.getWorld(worldUUID);
        }
        return Bukkit.getWorld(world);
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getStringWorld() {
        if (world == null) {
            return "null";
        }

        return world;
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%s;%s", getStringX(), getStringY(), getStringZ(), getStringPitch(), getStringYaw(), getStringWorld());
    }

    public OpLocation getHighestYLocation() {
        Location location = getLocation();
        location.setY(getWorld().getHighestBlockYAt((int) getX(), (int) getZ()) + 1);
        return new OpLocation(location);
    }

    public String toFamilyString() {
        return String.format("X: %s Y: %s Z: %s World: %s", getShortX(), getShortY(), getShortZ(), getStringWorld());
    }

    public String toFamilyStringWithoutWorld() {
        return String.format("X: %s Y: %s Z: %s", getShortX(), getShortY(), getShortZ());
    }

    public boolean isNotValid() {
        return toString().equals("0.0;0.0;0.0;0.0;0.0;null");
    }

    public void setWorldUUID(UUID worldUUID) {
        this.worldUUID = worldUUID;
    }

    public boolean hasTheSameBlock(@NotNull OpLocation location) {
        return hasTheSameBlockOnSetYAxis(location) && location.getBlockY() == getBlockY();
    }

    public boolean hasTheSameBlockOnSetYAxis(@NotNull OpLocation location) {
        return location.getBlockX() == getBlockX() && location.getBlockZ() == getBlockZ();
    }

    public static OpLocation deserialize(Map<String, Object> map) {
        double x = (double) map.get("x");
        double y = (double) map.get("y");
        double z = (double) map.get("z");
        float pitch = ((Number) map.get("pitch")).floatValue();
        float yaw = ((Number) map.get("yaw")).floatValue();
        String worldName = (String) map.get("world");
        return new OpLocation(x, y, z, pitch, yaw, Bukkit.getWorld(worldName));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("pitch", pitch);
        map.put("yaw", yaw);
        map.put("world", world);
        return map;
    }
}
