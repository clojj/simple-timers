package de.clojj.simpletimers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelayQueueSchedulerTest {

    public static final int DELAY_NANOS = 500000000;

    private DelayQueueScheduler delayQueueScheduler;
    private int consumed;

    @BeforeEach
    void setUp() {
        delayQueueScheduler = new DelayQueueScheduler(true);
        delayQueueScheduler.debugPrint("initial timers:");
    }

    @AfterEach
    void tearDown() {
        delayQueueScheduler.stop();
    }

    @Test
    void test_non_repeating() throws InterruptedException {
        delayQueueScheduler.add(new TimerObjectMillis(5000, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectMillis(1000, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectMillis(500, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectMillis(500, false, this::consumer));
        delayQueueScheduler.add(new TimerObjectMillis(500, false, this::consumer));

        delayQueueScheduler.debugPrint(null);
        Thread.sleep(3000);
        delayQueueScheduler.debugPrint(null);
        assertEquals(4, consumed);

        delayQueueScheduler.debugPrint(null);
        Thread.sleep(2000);
        assertEquals(5, consumed);

        delayQueueScheduler.debugPrint(null);
    }

    @Test
    void test_non_repeating_coninciding_ordered() throws InterruptedException {
        List<Integer> results = new ArrayList<>();
        delayQueueScheduler.add(new TimerObjectNano(DELAY_NANOS, false, aLong -> {
            System.out.println("2 - " + aLong);
            results.add(2);
        }));
        delayQueueScheduler.add(new TimerObjectNano(DELAY_NANOS, false, aLong -> {
            System.out.println("3 - " + aLong);
            results.add(3);
        }));
        delayQueueScheduler.add(new TimerObjectNano(DELAY_NANOS, false, aLong -> {
            System.out.println("1 - " + aLong);
            results.add(1);
        }));

        delayQueueScheduler.debugPrint(null);
        Thread.sleep(2000);
        delayQueueScheduler.debugPrint(null);

        List<Integer> expected = new ArrayList<>();
        Collections.addAll(expected, 2, 3, 1);
        assertEquals(expected, results);
    }

    @Test
    void test_repeating() throws InterruptedException {
        delayQueueScheduler.add(new TimerObjectMillis(1000, true, this::consumer));
        delayQueueScheduler.debugPrint(null);
        Thread.sleep(2000);
        assertEquals(1, consumed);
        delayQueueScheduler.debugPrint(null);
        Thread.sleep(1000);
        assertEquals(2, consumed);
        delayQueueScheduler.debugPrint(null);
    }

    private void consumer(Long time) {
        System.out.println("    time = " + time);
        consumed++;
    }

    @Test
    public void currying() {
        // Create a function that adds 2 ints
        IntBinaryOperator adder = (a, b) -> a + b;

        // And a function that takes an integer and returns a function
        IntFunction<IntUnaryOperator> currier = a -> b -> adder.applyAsInt(a, b);

        // Call apply 4 to currier (to get a function back)
        IntUnaryOperator curried = currier.apply(4);

        // Results
        System.out.printf("int curry : %d\n", curried.applyAsInt(3)); // ( 4 + 3 )

        // TODO
        LongBinaryOperator lAdder = (a, b) -> a + b;
        LongFunction<LongConsumer> lCurrier = a -> b -> lAdder.applyAsLong(a, b);
        LongConsumer lCurried = lCurrier.apply(1);

        lCurried.accept(3);


    }
}