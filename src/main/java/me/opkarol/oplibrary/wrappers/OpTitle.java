package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IgnoreInject
@SerializableAs("OpTitle")
public class OpTitle implements Serializable, ConfigurationSerializable {
    private String title;
    private String subtitle;
    private transient String tempTitle;
    private transient String tempSubTitle;
    private int fadeIn, fadeOut, stay;
    private transient List<Player> receivers;

    public OpTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subTitle;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.stay = stay;
    }

    public OpTitle(String title, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.stay = stay;
    }

    public OpTitle() {

    }

    public String getTitle() {
        return tempTitle == null ? title : tempTitle;
    }

    public OpTitle setTitle(String title) {
        this.title = title;
        return this;
    }

    public OpTitle setTitle(String change, String changeInto) {
        this.tempTitle = title.replace(change, changeInto);
        return this;
    }

    public String getSubTitle() {
        return tempSubTitle == null ? subtitle : tempSubTitle;
    }

    public OpTitle setSubtitle(String change, String changeInto) {
        return setSubtitle(subtitle.replace(change, changeInto));
    }

    public OpTitle setSubtitle(String subTitle) {
        this.subtitle = subTitle;
        return this;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public OpTitle setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public void clearTempString() {
        this.tempTitle = null;
        this.tempSubTitle = null;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public OpTitle setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public int getStay() {
        return stay;
    }

    public OpTitle setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public List<Player> getReceivers() {
        return receivers;
    }

    public OpTitle setReceivers(List<Player> receivers) {
        this.receivers = receivers;
        return this;
    }

    public OpTitle addReceiver(Player player) {
        List<Player> list = getReceivers();
        if (getReceivers() == null) {
            list = new ArrayList<>();
        }
        list.add(player);
        setReceivers(list);
        return this;
    }

    public OpTitle display(@NotNull Player player) {
        player.sendTitle(FormatTool.formatMessage(getTitle()), FormatTool.formatMessage(getSubTitle()), fadeIn, stay, fadeOut);
        clearTempString();
        return this;
    }

    public OpTitle display(List<Player> players) {
        if (players == null) {
            return this;
        }

        players.forEach(this::display);
        return this;
    }

    public OpTitle display() {
        return display(receivers);
    }

    @Override
    public String toString() {
        return "OpTitle{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", tempTitle='" + tempTitle + '\'' +
                ", tempSubTitle='" + tempSubTitle + '\'' +
                ", fadeIn=" + fadeIn +
                ", fadeOut=" + fadeOut +
                ", stay=" + stay +
                ", receivers=" + receivers +
                '}';
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("subtitle", subtitle);
        map.put("fadeIn", fadeIn);
        map.put("stay", stay);
        map.put("fadeOut", fadeOut);
        return map;
    }

    public static @NotNull OpTitle deserialize(@NotNull Map<String, Object> map) {
        OpTitle opTitle = new OpTitle();
        opTitle.setTitle((String) map.get("title"));
        opTitle.setSubtitle((String) map.get("subtitle"));
        opTitle.setFadeIn((int) map.get("fadeIn"));
        opTitle.setStay((int) map.get("stay"));
        opTitle.setFadeOut((int) map.get("fadeOut"));
        return opTitle;
    }
}
