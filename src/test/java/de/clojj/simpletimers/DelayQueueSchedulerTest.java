package de.clojj.simpletimers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DelayQueueSchedulerTest {

    public static final int DELAY_NANOS = 500000000;

    private DelayQueueScheduler delayQueueScheduler;
    private int consumed;

    @BeforeEach
    void setUp() {
        delayQueueScheduler = new DelayQueueScheduler(true, true);
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

        delayQueueScheduler.debugPrint();
        Thread.sleep(2000);
        delayQueueScheduler.debugPrint();
        assertEquals(4, consumed);

        delayQueueScheduler.debugPrint();
        Thread.sleep(4000);
        assertEquals(5, consumed);

        delayQueueScheduler.debugPrint();
    }

    @Test
    void test_non_repeating_coninciding_ordered() throws InterruptedException {
        List<Integer> results = new ArrayList<>();
        delayQueueScheduler.add(new TimerObjectNano(DELAY_NANOS, false, createNumberedConsumer(2, results)));
        delayQueueScheduler.add(new TimerObjectNano(DELAY_NANOS, false, createNumberedConsumer(3, results)));
        delayQueueScheduler.add(new TimerObjectNano(DELAY_NANOS, false, createNumberedConsumer(1, results)));

        delayQueueScheduler.debugPrint();
        Thread.sleep(2000);
        delayQueueScheduler.debugPrint();

        List<Integer> expected = new ArrayList<>();
        Collections.addAll(expected, 2, 3, 1);
        assertEquals(expected, results);
    }

    @Test
    void test_repeating() throws InterruptedException {
        delayQueueScheduler.add(new TimerObjectMillis(1000, true, this::consumer));
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

    Consumer<Long> createNumberedConsumer(int n, List<Integer> result) {
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