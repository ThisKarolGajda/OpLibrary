package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.location.OpLocation;
import me.opkarol.oplibrary.misc.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IgnoreInject
@SerializableAs("OpSound")
public class OpSound implements Serializable, ConfigurationSerializable {
    private double volume = 1, pitch = 1;
    private Sound sound;
    private SoundCategory category;
    private transient List<Player> receivers;
    private transient OpLocation location;

    public OpSound(Sound sound) {
        this.sound = sound;
    }

    public OpSound() {
        this.sound = null;
    }

    public OpSound(String sound) {
        StringUtil.getEnumValue(sound, Sound.class).ifPresent(sound1 -> this.sound = sound1);
    }

    public Sound getSound() {
        return sound;
    }

    public OpSound setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public OpSound playToAllOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            play(player);
        }
        return this;
    }

    public OpSound play() {
        return play(getLocation());
    }

    public OpSound play(Location location) {
        return play(new OpLocation(location));
    }

    public OpSound play(OpLocation location) {
        if (receivers == null) {
            Bukkit.getOnlinePlayers().forEach(player -> play(player, location.getLocation()));
            return this;
        }

        receivers.forEach(player -> play(player, location.getLocation()));
        return this;
    }

    public OpSound play(@NotNull Player player, @NotNull Location location) {
        if (sound == null) {
            return this;
        }
        if (category == null) {
            player.playSound(location, sound, (float) volume, (float) pitch);
        } else {
            player.playSound(location, sound, category, (float) volume, (float) pitch);
        }
        return this;
    }

    public OpSound play(@NotNull Player player) {
        return play(player, player.getLocation());
    }

    public SoundCategory getCategory() {
        return category;
    }

    public OpSound setCategory(SoundCategory category) {
        this.category = category;
        return this;
    }

    public OpSound setReceivers(List<Player> receivers) {
        this.receivers = receivers;
        return this;
    }

    public OpLocation getLocation() {
        return location;
    }

    public OpSound setLocation(OpLocation location) {
        this.location = location;
        return this;
    }

    public OpSound setLocation(Location location) {
        this.location = new OpLocation(location);
        return this;
    }


    public OpSound setPitch(double pitch) {
        this.pitch = pitch;
        return this;
    }

    public OpSound setVolume(double volume) {
        this.volume = volume;
        return this;
    }

    @Override
    public String toString() {
        return "OpSound{" +
                "volume=" + volume +
                ", pitch=" + pitch +
                ", sound=" + sound +
                ", category=" + category +
                ", receivers=" + receivers +
                ", location=" + location +
                '}';
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("sound", sound != null ? sound.name() : null);
        map.put("category", category != null ? category.name() : null);
        map.put("volume", volume);
        map.put("pitch", pitch);
        map.put("location", location);
        return map;
    }

    public static @NotNull OpSound deserialize(@NotNull Map<String, Object> map) {
        OpSound opSound = new OpSound();
        String soundName = (String) map.get("sound");
        if (soundName != null) {
            opSound.setSound(Sound.valueOf(soundName));
        }
        String categoryName = (String) map.get("category");
        if (categoryName != null) {
            opSound.setCategory(SoundCategory.valueOf(categoryName));
        }
        opSound.setVolume((double) map.get("volume"));
        opSound.setPitch((double) map.get("pitch"));
        opSound.setLocation((OpLocation) map.get("location"));
        return opSound;
    }
}
