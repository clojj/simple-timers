package de.clojj.simpletimers;

import java.util.concurrent.Delayed;
import java.util.function.Consumer;

public interface TimerObject extends Delayed {

    String getId();
    void reset();
    Consumer<Long> getConsumer();
    boolean isRepeat();
}
