package me.opkarol.oplibrary.location;

import me.opkarol.oporm.SerializableFieldOrm;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public class OpSerializableLocation implements Serializable, SerializableFieldOrm {
    private double x, y, z;
    private float pitch, yaw;
    private String world;
    private UUID worldUUID;
    private OpSerializableLocation lastLocation;

    public OpSerializableLocation(double x, double y, double z, float pitch, float yaw, World world) {
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

    public OpSerializableLocation(@NotNull Location location) {
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

    public OpSerializableLocation(String string) {
        if (string != null && string.length() != 0) {
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

    public OpSerializableLocation(@NotNull OpLocation location) {
        this(location.toString());
    }

    public OpSerializableLocation() {

    }

    public double getX() {
        return x;
    }
    public int getBlockX() {
        return Math.toIntExact(Math.round(getX()));
    }
    public void setX(double x) {
        this.x = x;
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
    public int getBlockY() {
        return Math.toIntExact(Math.round(getY()));
    }

    public void setY(double y) {
        this.y = y;
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
    public int getBlockZ() {
        return Math.toIntExact(Math.round(getZ()));
    }
    public void setZ(double z) {
        this.z = z;
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

    public void setLastLocation(OpSerializableLocation lastLocation) {
        this.lastLocation = lastLocation;
    }

    public OpSerializableLocation setLastLocation() {
        this.lastLocation = new OpSerializableLocation(toString());
        return this.lastLocation;
    }

    public OpLocation toLocation() {
        return new OpLocation(toString());
    }

    public OpSerializableLocation getHighestYLocation() {
        Location location = getLocation();
        location.setY(getWorld().getHighestBlockYAt((int) getX(), (int) getZ()) + 1);
        return new OpSerializableLocation(location);
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

    public boolean hasTheSameBlock(@NotNull OpSerializableLocation location) {
        return hasTheSameBlockOnSetYAxis(location) && location.getBlockY() == getBlockY();
    }

    public boolean hasTheSameBlockOnSetYAxis(@NotNull OpSerializableLocation location) {
        return location.getBlockX() == getBlockX() && location.getBlockZ() == getBlockZ();
    }

    @Override
    public String serialize() {
        return toString();
    }

    @Override
    public Object deserialize(String s) {
        return new OpSerializableLocation(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpSerializableLocation that = (OpSerializableLocation) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Float.compare(that.pitch, pitch) == 0 && Float.compare(that.yaw, yaw) == 0 && Objects.equals(world, that.world) && Objects.equals(worldUUID, that.worldUUID) && Objects.equals(lastLocation, that.lastLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, pitch, yaw, world, worldUUID, lastLocation);
    }
}
