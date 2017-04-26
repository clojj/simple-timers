package de.clojj.simpletimers;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TimerObjectMillis implements TimerObject {
    private final long delayFromNow;
    private long startTime;
    private final boolean repeat;
    private final Consumer<Long> consumer;

    public TimerObjectMillis(long delayFromNow, boolean repeat, Consumer<Long> consumer) {
        this.delayFromNow = delayFromNow;
        this.startTime = System.currentTimeMillis() + this.delayFromNow;
        this.repeat = repeat;
        this.consumer = consumer;
    }

    @Override
    public void reset() {
        this.startTime = System.currentTimeMillis() + delayFromNow;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.startTime < ((TimerObjectMillis) o).startTime) {
            return -1;
        }
        if (this.startTime > ((TimerObjectMillis) o).startTime) {
            return 1;
        }
        return 0;
    }

    @Override
    public Consumer<Long> getConsumer() {
        return consumer;
    }

    @Override
    public boolean isRepeat() {
        return repeat;
    }

    @Override
    public String toString() {
        return "DelayObject{" +
                "delayFromNow=" + delayFromNow +
                ", startTime=" + startTime +
                ", repeat=" + repeat +
                '}';
    }
}