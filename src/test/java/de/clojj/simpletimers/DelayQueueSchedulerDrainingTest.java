package de.clojj.simpletimers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DelayQueueSchedulerDrainingTest {

    public static final int DELAY_NANOS = 500000000;

    private DelayQueueScheduler delayQueueScheduler;
    private int consumed;

    @BeforeEach
    void setUp() {
	    delayQueueScheduler = new DelayQueueScheduler( );
	    delayQueueScheduler.debugPrint("initial timers:");
    }

    @Test
    void test_by_draining() throws InterruptedException {
	    Consumer<Long> devNull = aLong -> {};
	    delayQueueScheduler.add(new TimerObjectInterval(5000, TimeUnit.MILLISECONDS, false, devNull));
	    delayQueueScheduler.add(new TimerObjectInterval(1000, TimeUnit.MILLISECONDS, false, devNull));
	    delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, devNull));
	    delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, devNull));
	    delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, devNull));
	    delayQueueScheduler.debugPrint();

	    Thread.sleep(2000);
	    Collection<TimerObject> drained = delayQueueScheduler.drainAllTimers();
        delayQueueScheduler.debugPrint("after draining " + drained);

        assertEquals(4, drained.size());
        assertEquals(1, delayQueueScheduler.size());
    }
}