package de.clojj.simpletimers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelayQueueSchedulerTest {

    private DelayQueueScheduler delayQueueScheduler;
    private int consumed;

    @BeforeEach
    void setUp() {
        delayQueueScheduler = new DelayQueueScheduler(true);
    }

    @AfterEach
    void tearDown() {
        delayQueueScheduler.stop();
    }

    @Test
    void test_non_repeating() throws InterruptedException {
        delayQueueScheduler.add(new TimerObject(5000, false, this::consumer));
        delayQueueScheduler.add(new TimerObject(1000, false, this::consumer));
        delayQueueScheduler.add(new TimerObject(500, false, this::consumer));

        delayQueueScheduler.debugPrint();
        Thread.sleep(3000);
        assertEquals(2, consumed);

        delayQueueScheduler.debugPrint();
        Thread.sleep(2000);
        assertEquals(3, consumed);

        delayQueueScheduler.debugPrint();
    }

    @Test
    void test_repeating() throws InterruptedException {
        delayQueueScheduler.add(new TimerObject(1000, true, this::consumer));
        delayQueueScheduler.debugPrint();
        Thread.sleep(2000);
        assertEquals(1, consumed);
        delayQueueScheduler.debugPrint();
        Thread.sleep(1000);
        assertEquals(2, consumed);
        delayQueueScheduler.debugPrint();
    }

    private void consumer(Long time) {
        System.out.println("time = " + time);
        consumed++;
    }
}