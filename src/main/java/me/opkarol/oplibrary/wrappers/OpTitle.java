package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class OpTitle {
    private String title;
    private String subtitle;
    private String tempTitle;
    private String tempSubTitle;
    private int fadeIn, fadeOut, stay;
    private List<Player> receivers;

    public OpTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subTitle;
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
}
