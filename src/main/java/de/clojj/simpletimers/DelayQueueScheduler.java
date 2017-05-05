package de.clojj.simpletimers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.DelayQueue;

public class DelayQueueScheduler {

	private boolean shutdown = false;

	private Thread thread = null;

	private final DelayQueue<TimerObject> delayQueue;

	private SortedMap<TimerObject, Long> timers = new ConcurrentSkipListMap<>();

	private TimerThread timerThread;


	public DelayQueueScheduler() {
		delayQueue = new DelayQueue<>();
	}

	public Thread createDefaultThread(boolean isDaemon, Runnable runnable) {
		Thread thread = new Thread(runnable, "DelayQueueScheduler thread");
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(isDaemon);
		return thread;
	}

	public SortedMap<TimerObject, Long> getTimers() {
		return timers;
	}

	public Collection<TimerObject> drainAllTimers() {
	    final Collection<TimerObject> expiredList = new ArrayList<>();
		delayQueue.drainTo(expiredList);
		return expiredList;
	}

	public boolean add(TimerObject timerObject) {
		timers.put(timerObject, System.nanoTime());
		return delayQueue.add(timerObject);
	}

	public void startWith(Thread thread) {
		this.thread = thread;
		thread.start();
	}

	public void stop() {
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

	public TimerThread timerThreadInstance() {
		if (timerThread == null) {
			timerThread = new TimerThread();
		}
		return timerThread;

	}

	public class TimerThread implements Runnable {

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
