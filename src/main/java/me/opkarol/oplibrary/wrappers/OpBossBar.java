package me.opkarol.oplibrary.wrappers;

import me.opkarol.oplibrary.runnable.OpRunnable;
import me.opkarol.oplibrary.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class OpBossBar implements Serializable {
    private BossBar bossBar;
    private final String originalTitle;
    private String title;
    private BarStyle barStyle;
    private BarColor barColor;
    private OpRunnable runnable;

    public OpBossBar(String title) {
        this.originalTitle = title;
        this.title = title;
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

    public OpBossBar setTitle(String title) {
        this.title = title;
        return build();
    }

    public OpBossBar setBarColor(BarColor barColor) {
        this.barColor = barColor;
        return this;
    }

    public OpBossBar setBarStyle(BarStyle barStyle) {
        this.barStyle = barStyle;
        return this;
    }

    public OpBossBar setBossBar(BossBar bossBar) {
        this.bossBar = bossBar;
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
        setVisible(true);
        return this;
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

    public OpBossBar loopAndDisplay(int time, int speed, Consumer<OpBossBar> onEndConsumer) {
        setVisible(true);
        AtomicReference<Double> current = new AtomicReference<>((double) time);
        setRunnable(new OpRunnable((r) -> {
            current.set(current.get() - 1);
            if (current.get() < 1) {
                removeAllPlayers();
                setVisible(false);
                onEndConsumer.accept(this);
                r.cancelTask();
            }
            setProgress(current.get() * speed / time);
        }).runTaskTimerAsynchronously(0, 20/speed));
        return this;
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

    public BarStyle getBarStyle() {
        return barStyle == null ? BarStyle.SOLID : barStyle;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public String getTitle() {
        return FormatTool.formatMessage(title);
    }

    public OpRunnable getRunnable() {
        return runnable;
    }

    public void setRunnable(OpRunnable runnable) {
        this.runnable = runnable;
    }

    public OpBossBar removeAllPlayers() {
        bossBar.getPlayers().forEach(this::removeDisplay);
        return this;
    }
}