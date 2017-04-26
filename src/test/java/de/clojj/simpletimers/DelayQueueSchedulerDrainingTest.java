package de.clojj.simpletimers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DelayQueueSchedulerDrainingTest {

    public static final int DELAY_NANOS = 500000000;

    private DelayQueueScheduler delayQueueScheduler;
    private int consumed;

    @BeforeEach
    void setUp() {
	    delayQueueScheduler = new DelayQueueScheduler(false, false);
	    delayQueueScheduler.debugPrint("initial timers:");
    }

    @Test
    void test_by_draining() throws InterruptedException {
	    delayQueueScheduler.add(new TimerObjectMillis(5000, false, this::consumer));
	    delayQueueScheduler.add(new TimerObjectMillis(1000, false, this::consumer));
	    delayQueueScheduler.add(new TimerObjectMillis(500, false, this::consumer));
	    delayQueueScheduler.add(new TimerObjectMillis(500, false, this::consumer));
	    delayQueueScheduler.add(new TimerObjectMillis(500, false, this::consumer));

	    delayQueueScheduler.debugPrint();
	    Thread.sleep(2000);
	    int drained = delayQueueScheduler.drainAllTimers();
        delayQueueScheduler.debugPrint("after draining " + drained);
    }

    private void consumer(Long time) {
        System.out.println("    time = " + time);
        consumed++;
    }

}