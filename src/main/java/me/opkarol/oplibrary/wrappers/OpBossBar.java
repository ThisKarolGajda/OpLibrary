package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.injection.IgnoreInject;
import me.opkarol.oplibrary.runnable.OpRunnable;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IgnoreInject
@SerializableAs("OpBossBar")
public class OpBossBar implements ConfigurationSerializable {
    private transient final String originalTitle;
    private transient BossBar bossBar;
    private String title;
    private BarStyle barStyle;
    private BarColor barColor;

    public OpBossBar(String title) {
        this.originalTitle = title;
        this.title = title;
        this.barStyle = BarStyle.SOLID;
        this.barColor = BarColor.WHITE;
    }

    public OpBossBar() {
        this.originalTitle = null;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public OpBossBar build() {
        bossBar = Bukkit.createBossBar(getTitle(), getBarColor(), getBarStyle());
        return this;
    }

    public OpBossBar setVisible(boolean visible) {
        if (bossBar != null) {
            bossBar.setVisible(visible);
        }
        return this;
    }

    public OpBossBar display(List<Player> players) {
        players.forEach(this::display);
        return this;
    }

    public OpBossBar display(Player player) {
        bossBar.addPlayer(player);
        return setVisible(true);
    }

    public OpBossBar removeDisplay(Player player) {
        bossBar.removePlayer(player);
        return this;
    }

    /**
     * @deprecated OpBossBar#displayAndRemoveAfter
     */
    @Deprecated
    public OpBossBar displayAndRemove(Player player, int ticks) {
        if (bossBar == null) {
            build();
        }

        bossBar.addPlayer(player);
        bossBar.setVisible(true);
        new OpRunnable(() -> {
            bossBar.removePlayer(player);
            if (bossBar.isVisible()) {
                bossBar.setVisible(false);
            }
        }).runTaskLaterAsynchronously(ticks);

        return this;
    }

    public void displayAndRemoveAfter(List<? extends Player> playerList, int ticks) {
        if (bossBar == null) {
            build();
        }

        playerList.forEach(player -> bossBar.addPlayer(player));
        bossBar.setVisible(true);

        new OpRunnable(() -> {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }).runTaskLaterAsynchronously(ticks);

    }

    public OpBossBar setProgress(double v) {
        if (v < 0) {
            v = 0;
        } else if (v > 1) {
            v = 1;
        }

        bossBar.setProgress(v);
        return this;
    }

    public BarColor getBarColor() {
        return barColor == null ? BarColor.WHITE : barColor;
    }

    public OpBossBar setBarColor(BarColor barColor) {
        this.barColor = barColor;
        return this;
    }

    public BarStyle getBarStyle() {
        return barStyle == null ? BarStyle.SOLID : barStyle;
    }

    public OpBossBar setBarStyle(BarStyle barStyle) {
        this.barStyle = barStyle;
        return this;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public OpBossBar setBossBar(BossBar bossBar) {
        this.bossBar = bossBar;
        return this;
    }

    public String getTitle() {
        return FormatTool.formatMessage(title);
    }

    public OpBossBar setTitle(String title) {
        this.title = title;
        return build();
    }

    public OpBossBar removeAllPlayers() {
        bossBar.getPlayers().forEach(this::removeDisplay);
        return this;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("barColor", barColor.name());
        map.put("barStyle", barStyle.name());
        return map;
    }

    public static @NotNull OpBossBar deserialize(@NotNull Map<String, Object> map) {
        String title = (String) map.get("title");
        BarColor barColor = BarColor.valueOf((String) map.get("barColor"));
        BarStyle barStyle = BarStyle.valueOf((String) map.get("barStyle"));
        OpBossBar opBossBar = new OpBossBar(title);
        opBossBar.setBarColor(barColor);
        opBossBar.setBarStyle(barStyle);
        return opBossBar;
    }
}