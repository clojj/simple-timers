package de.clojj.simpletimers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelayQueueWaiterTest {

    private DelayQueueWaiter delayQueueWaiter;
    private int consumed;

    @BeforeEach
    void setUp() {
        delayQueueWaiter = new DelayQueueWaiter(true);
    }

    @AfterEach
    void tearDown() {
        delayQueueWaiter.stop();
    }

    @Test
    void test_non_repeating() throws InterruptedException {
        delayQueueWaiter.add(new DelayObject(5000, false, this::consumer));
        delayQueueWaiter.add(new DelayObject(1000, false, this::consumer));
        delayQueueWaiter.add(new DelayObject(500, false, this::consumer));

        delayQueueWaiter.debugPrint();
        Thread.sleep(3000);
        assertEquals(2, consumed);

        delayQueueWaiter.debugPrint();
        Thread.sleep(2000);
        assertEquals(3, consumed);

        delayQueueWaiter.debugPrint();
    }

    @Test
    void test_repeating() throws InterruptedException {
        delayQueueWaiter.add(new DelayObject(1000, true, this::consumer));
        delayQueueWaiter.debugPrint();
        Thread.sleep(2000);
        assertEquals(1, consumed);
        delayQueueWaiter.debugPrint();
        Thread.sleep(1000);
        assertEquals(2, consumed);
        delayQueueWaiter.debugPrint();
    }

    private void consumer(Long time) {
        System.out.println("time = " + time);
        consumed++;
    }
}