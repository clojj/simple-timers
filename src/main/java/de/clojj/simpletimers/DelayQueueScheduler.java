package de.clojj.simpletimers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueueScheduler {

	private boolean shutdown = false;

	private Thread thread = null;

	private final DelayQueue<TimerObject> delayQueue;

	private final transient ReentrantLock lock = new ReentrantLock();


	public DelayQueueScheduler(boolean start, boolean isDaemon) {
		delayQueue = new DelayQueue<>();
		if (start) {
			thread = new Thread(new Waiter(), this.getClass().getName());
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.setDaemon(isDaemon);
			thread.start();
		}
	}

	public Collection<TimerObject> drainAllTimers() {
	    final Collection<TimerObject> expiredList = new ArrayList<>();
		delayQueue.drainTo(expiredList);
		return expiredList;
	}

	public synchronized boolean add(TimerObject timerObject) {
		return delayQueue.add(timerObject);
	}

	public synchronized void stop() {
		shutdown = true;
		thread.interrupt();
	}

	public void debugPrint() {
		debugPrint("timers:");
	}

	public void debugPrint(String message) {
		System.out.println(message != null ? message : "timers:");
		for (TimerObject timerObject : delayQueue) {
			System.out.println("delayObject = " + timerObject);
		}
	}

	public boolean deactivate(final TimerObject toDeactivate) {
		return delayQueue.remove(toDeactivate);
	}

	public int size() {
		return delayQueue.size();
	}

	private class Waiter implements Runnable {

		public void run() {
			try {
				while (!Thread.interrupted()) {
					TimerObject timerObject = delayQueue.take();

					// 1) callback object
					timerObject.getConsumer().accept(System.nanoTime());

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
