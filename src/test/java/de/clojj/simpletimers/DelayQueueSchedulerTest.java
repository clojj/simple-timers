package de.clojj.simpletimers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelayQueueSchedulerTest {

    public static final int DELAY_NANOS = 500000000;

    private DelayQueueScheduler delayQueueScheduler;
    private int consumed;

    @BeforeEach
    void setUp() {
        delayQueueScheduler = new DelayQueueScheduler();
        Thread defaultThread = delayQueueScheduler.createDefaultThread(true, delayQueueScheduler.timerThreadInstance());
        delayQueueScheduler.startWith(defaultThread);
        delayQueueScheduler.debugPrint("initial timers:");
    }

    @AfterEach
    void tearDown() {
        delayQueueScheduler.stop();
    }

    @Test
    void test_non_repeating() throws InterruptedException {
        delayQueueScheduler.add(new TimerObjectInterval(5000, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectInterval(1000, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.debugPrint();

        Thread.sleep(2000);
        delayQueueScheduler.debugPrint();
        assertEquals(4, consumed);

        Thread.sleep(4000);
        delayQueueScheduler.debugPrint();
        assertEquals(5, consumed);
    }

    @Test
    void test_deactivate_by_removing() throws InterruptedException {
        TimerObjectInterval timerObjectToDeactivate = new TimerObjectInterval(5000, TimeUnit.MILLISECONDS, false, this::consumer);
        delayQueueScheduler.add(timerObjectToDeactivate);
        delayQueueScheduler.add(new TimerObjectInterval(1000, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectInterval(500, TimeUnit.MILLISECONDS, false, this::consumer));
        delayQueueScheduler.debugPrint();

        Thread.sleep(2000);
        delayQueueScheduler.debugPrint();
        assertEquals(4, consumed);

        boolean deactivated = delayQueueScheduler.deactivate(timerObjectToDeactivate);
        assertTrue(deactivated);
        delayQueueScheduler.debugPrint();

        Thread.sleep(4000);
        delayQueueScheduler.debugPrint();
        assertEquals(4, consumed);
    }

    @Test
    void test_coninciding_ordered() throws InterruptedException {
        List<Integer> results = new ArrayList<>();
        delayQueueScheduler.add(new TimerObjectInterval(DELAY_NANOS, TimeUnit.NANOSECONDS, false, createNumberedConsumer(2, results)));
        delayQueueScheduler.add(new TimerObjectInterval(DELAY_NANOS, TimeUnit.NANOSECONDS, false, createNumberedConsumer(3, results)));
        delayQueueScheduler.add(new TimerObjectInterval(DELAY_NANOS, TimeUnit.NANOSECONDS, false, createNumberedConsumer(1, results)));

        delayQueueScheduler.debugPrint();
        Thread.sleep(2000);
        delayQueueScheduler.debugPrint();

        List<Integer> expected = new ArrayList<>();
        Collections.addAll(expected, 2, 3, 1);
        assertEquals(expected, results);
    }

    @Test
    void test_repeating() throws InterruptedException {
        delayQueueScheduler.add(new TimerObjectInterval(1000, TimeUnit.MILLISECONDS, true, this::consumer));
        delayQueueScheduler.debugPrint();
        Thread.sleep(2000);
        assertEquals(1, consumed);
        delayQueueScheduler.debugPrint();
        Thread.sleep(1000);
        assertEquals(2, consumed);
        delayQueueScheduler.debugPrint();
    }

    private void consumer(Long time) {
        System.out.println("    time = " + time);
        consumed++;
    }

    private Consumer<Long> createNumberedConsumer(int n, List<Integer> result) {
        return aLong -> {
            System.out.println("consumer " + n + " receives " + aLong);
            result.add(n);
        };
    }

    public void currying() {
        // Create a function that adds 2 ints
        IntBinaryOperator adder = (a, b) -> a + b;

        // And a function that takes an integer and returns a function
        IntFunction<IntUnaryOperator> currier = a -> b -> adder.applyAsInt(a, b);

        // Call apply 4 to currier (to get a function back)
        IntUnaryOperator curried = currier.apply(4);

        // Results
        System.out.printf("int curry : %d\n", curried.applyAsInt(3)); // ( 4 + 3 )
    }
}