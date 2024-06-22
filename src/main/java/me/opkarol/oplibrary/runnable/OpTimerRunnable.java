package me.opkarol.oplibrary.runnable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class OpTimerRunnable {

    public OpTimerRunnable() {

    }

    public OpTimerRunnable(Runnable runnable, int times) {
        run(runnable, times);
    }

    public OpTimerRunnable(Consumer<OpRunnable> runnableConsumer, int times) {
        run(runnableConsumer, times);
    }

    public synchronized void runTaskTimesDown(BiConsumer<OpRunnable, Integer> onEachConsumer, Consumer<OpRunnable> onEndConsumer, int times) {
        final int[] i = {times};
        new OpRunnable(r -> {
            if (i[0] < 1) {
                onEndConsumer.accept(r);
                r.cancelTask();
                return;
            }

            onEachConsumer.accept(r, i[0]);
            i[0]--;
        }).runTaskTimer(0, 20);
    }

    public synchronized void run(Runnable runnable, int times) {
        final int[] i = {times};
        new OpRunnable(r -> {
            if (i[0] < 1) {
                r.cancelTask();
                return;
            }

            runnable.run();
            i[0]--;
        }).runTaskTimer(0, 20);
    }

    public synchronized void run(Consumer<OpRunnable> runnableConsumer, int times) {
        final int[] i = {times};
        new OpRunnable(r -> {
            if (i[0] < 1) {
                r.cancelTask();
                return;
            }

            runnableConsumer.accept(r);
            i[0]--;
        }).runTaskTimer(0, 20);
    }

    public synchronized void runTaskTimesUp(BiConsumer<OpRunnable, Integer> onEachConsumer, Consumer<OpRunnable> onEndConsumer, int times) {
        final int[] i = {1};
        new OpRunnable(r -> {
            if (i[0] >= times) {
                onEndConsumer.accept(r);
                r.cancelTask();
                return;
            }

            onEachConsumer.accept(r, i[0]);
            i[0]++;
        }).runTaskTimer(0, 20);
    }

    public OpRunnable runTaskTimesDownAsynchronously(BiConsumer<OpRunnable, Integer> onEachConsumer, Consumer<OpRunnable> onEndConsumer, int times) {
        final int[] i = {times};
        return new OpRunnable(r -> {
            if (i[0] < 1) {
                onEndConsumer.accept(r);
                r.cancelTask();
                return;
            }

            onEachConsumer.accept(r, i[0]);
            i[0]--;
        }).runTaskTimerAsynchronously(0, 20);
    }

    public OpRunnable runTaskTimesDownAsynchronously(BiConsumer<OpRunnable, Integer> onEachConsumer, int times) {
        final int[] i = {times};
        return new OpRunnable(r -> {
            if (i[0] < 1) {
                r.cancelTask();
                return;
            }

            onEachConsumer.accept(r, i[0]);
            i[0]--;
        }).runTaskTimerAsynchronously(0, 20);
    }

    public synchronized void runTaskTimesUpAsynchronously(BiConsumer<OpRunnable, Integer> onEachConsumer, Consumer<OpRunnable> onEndConsumer, int times) {
        final int[] i = {1};
        new OpRunnable(r -> {
            if (i[0] > times) {
                onEndConsumer.accept(r);
                r.cancelTask();
                return;
            }

            onEachConsumer.accept(r, i[0]);
            i[0]++;
        }).runTaskTimerAsynchronously(0, 20);
    }

    public synchronized void runTaskTimesUpAsynchronously(BiConsumer<OpRunnable, Integer> onEachConsumer, int times) {
        final int[] i = {1};
        new OpRunnable(r -> {
            if (i[0] > times) {
                r.cancelTask();
                return;
            }

            onEachConsumer.accept(r, i[0]);
            i[0]++;
        }).runTaskTimerAsynchronously(0, 20);
    }

    public void run(Runnable runnable, int times, int delay) {
        final int[] i = {times};
        new OpRunnable(r -> {
            if (i[0] < 1) {
                r.cancelTask();
                return;
            }

            runnable.run();
            i[0]--;
        }).runTaskTimer(0, delay);
    }

    public void run(Consumer<OpRunnable> consumer, int times, int delay) {
        final int[] i = {times};
        new OpRunnable(r -> {
            if (i[0] < 1) {
                r.cancelTask();
                return;
            }

            consumer.accept(r);
            i[0]--;
        }).runTaskTimer(0, delay);
    }
}
