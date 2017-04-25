package de.clojj.simpletimers;

import java.util.concurrent.DelayQueue;

public class DelayQueueWaiter {

    private boolean shutdown = false;

    private final Thread thread;

    private final DelayQueue<DelayObject> delayQueue;

    public DelayQueueWaiter(boolean isDaemon) {
        delayQueue = new DelayQueue<DelayObject>();

        thread = new Thread(new Waiter(), this.getClass().getName());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(isDaemon);
        thread.start();
    }

    public synchronized boolean add(DelayObject delayObject) {
        return delayQueue.add(delayObject);
    }

    public synchronized void stop() {
        shutdown = true;
        thread.interrupt();
    }

    public synchronized void debugPrint() {
        for (DelayObject delayObject : delayQueue) {
            System.out.println("delayObject = " + delayObject);
        }

    }

    private class Waiter implements Runnable {

        public void run() {
            try {
                while (!Thread.interrupted()) {
                    DelayObject delayObject = delayQueue.take();
                    if (delayObject.isRepeat()) {
                        delayObject.reset();
                        delayQueue.add(delayObject);
                    }
                    delayObject.getConsumer().accept(System.currentTimeMillis());
                }

            } catch (InterruptedException e) {
                if (!shutdown) {
                    e.printStackTrace();
                }
            }
        }

    }

}
