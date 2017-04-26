package de.clojj.simpletimers;

import java.util.concurrent.DelayQueue;

public class DelayQueueScheduler {

    private boolean shutdown = false;

    private final Thread thread;

    private final DelayQueue<TimerObject> delayQueue;

    public DelayQueueScheduler(boolean isDaemon) {
        delayQueue = new DelayQueue<>();

        thread = new Thread(new Waiter(), this.getClass().getName());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(isDaemon);
        thread.start();
    }

    public synchronized boolean add(TimerObject timerObject) {
        return delayQueue.add(timerObject);
    }

    public synchronized void stop() {
        shutdown = true;
        thread.interrupt();
    }

    public synchronized void debugPrint(String message) {
        System.out.println(message != null ? message : "timers:");
        for (TimerObject timerObject : delayQueue) {
            System.out.println("delayObject = " + timerObject);
        }
    }

    private class Waiter implements Runnable {

        public void run() {
            try {
                while (!Thread.interrupted()) {
                    TimerObject timerObject = delayQueue.take();

                    // 1) callback object
                    timerObject.getConsumer().accept(System.currentTimeMillis());

                    // 2) TODO create global callback
                    // this should enable monitoring all Timer-events (JMX, CDI, ...?)

                    if (timerObject.isRepeat()) {
                        timerObject.reset();
                        delayQueue.add(timerObject);
                    }
                }
            } catch (InterruptedException e) {
                if (!shutdown) {
                    e.printStackTrace();
                }
            }
        }
    }

}
