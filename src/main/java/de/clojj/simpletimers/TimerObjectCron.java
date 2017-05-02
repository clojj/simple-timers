package de.clojj.simpletimers;

import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.time.ExecutionTime;

import java.time.ZonedDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TimerObjectCron implements TimerObject {
	private long startTime;
	private final Consumer<Long> consumer;
	private Cron cron;
	private final boolean repeat;

	public TimerObjectCron(Cron cron, Consumer<Long> consumer) {
		this.cron = cron;
		this.consumer = consumer;
		this.startTime = ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toInstant().toEpochMilli();
		this.repeat = cron.retrieveFieldsAsMap().values().stream().anyMatch(cronField -> cronField.getExpression() instanceof Every);
	}

	@Override
	public void reset() {
		this.startTime = System.currentTimeMillis() + ExecutionTime.forCron(cron).timeToNextExecution(ZonedDateTime.now()).toMillis();
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long diff = startTime - System.currentTimeMillis();
		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed o) {
		if (this.startTime < ((TimerObjectCron) o).startTime) {
			return -1;
		}
		if (this.startTime > ((TimerObjectCron) o).startTime) {
			return 1;
		}
		return 0;
	}

	@Override
	public Consumer<Long> getConsumer() {
		return consumer;
	}

	@Override
	public boolean isRepeat() {
		return repeat;
	}

	@Override
	public String toString() {
		return "TimerObjectCron{" +
				"startTime=" + startTime +
				", cron=" + cron.asString() +
				", repeat=" + repeat +
				", consumer=" + consumer +
				'}';
	}
}