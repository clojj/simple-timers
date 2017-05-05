package de.clojj.simpletimers;

import java.time.ZonedDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.time.ExecutionTime;

public class TimerObjectCron implements TimerObject {
	private long startTime;
	private Consumer<Long> consumer;
	private Cron cron;
	private final boolean repeat;

	public TimerObjectCron(Cron cron, Consumer<Long> consumer) {
		this.cron = cron;
		this.startTime = ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toInstant().toEpochMilli();
		this.repeat = cron.retrieveFieldsAsMap().values().stream().anyMatch(cronField -> cronField.getExpression() instanceof Every);
		this.consumer = consumer;
	}

	public TimerObjectCron(Cron cron) {
		this.cron = cron;
		this.startTime = ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toInstant().toEpochMilli();
		this.repeat = cron.retrieveFieldsAsMap().values().stream().anyMatch(cronField -> cronField.getExpression() instanceof Every);
	}

	public void setConsumer(Consumer<Long> consumer) {
		this.consumer = consumer;
	}

	@Override
	public void reset() {
		this.startTime = ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toInstant().toEpochMilli();
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long delta = startTime - System.currentTimeMillis();
		return unit.convert(delta, TimeUnit.MILLISECONDS);
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

	public long getStartTime() {
		return startTime;
	}

	public Cron getCron() {
		return cron;
	}

	@Override
	public String toString() {
		return "TimerObjectCron{" +
				"startTime=" + startTime +
				", consumer=" + consumer +
				", cron=" + cron +
				", repeat=" + repeat +
				'}';
	}
}