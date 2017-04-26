package de.clojj.simpletimers;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TimerObjectNano implements TimerObject {
    private final long delayFromNow;
    private long startTime;
    private final boolean repeat;
    private final Consumer<Long> consumer;

    public TimerObjectNano(long delayFromNow, boolean repeat, Consumer<Long> consumer) {
        this.delayFromNow = delayFromNow;
        this.startTime = System.nanoTime() + this.delayFromNow;
        this.repeat = repeat;
        this.consumer = consumer;
    }

    public void reset() {
        this.startTime = System.nanoTime() + delayFromNow;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.nanoTime();
        return unit.convert(diff, TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.startTime < ((TimerObjectNano) o).startTime) {
            return -1;
        }
        if (this.startTime > ((TimerObjectNano) o).startTime) {
            return 1;
        }
        return 0;
    }

    public Consumer<Long> getConsumer() {
        return consumer;
    }

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