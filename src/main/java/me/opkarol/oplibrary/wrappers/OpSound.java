package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.location.OpSerializableLocation;
import me.opkarol.oplibrary.location.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class OpSound {
    private double volume = 1, pitch = 1;
    private Sound sound;
    private SoundCategory category;
    private List<Player> receivers;
    private OpSerializableLocation location;

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
        return play(new OpSerializableLocation(location));
    }

    public OpSound play(OpSerializableLocation location) {
        if (receivers == null) {
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

    public OpSerializableLocation getLocation() {
        return location;
    }

    public OpSound setLocation(OpSerializableLocation location) {
        this.location = location;
        return this;
    }

    public OpSound setLocation(Location location) {
        this.location = new OpSerializableLocation(location);
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
}
