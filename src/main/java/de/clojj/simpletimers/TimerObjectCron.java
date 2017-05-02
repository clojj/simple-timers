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
	private final Consumer<Long> consumer;
	private Cron cron;
	private final boolean repeat;

	public TimerObjectCron(Cron cron, Consumer<Long> consumer) {
		this.cron = cron;
		this.consumer = consumer;
		this.startTime = System.currentTimeMillis() + ExecutionTime.forCron(cron).timeToNextExecution(ZonedDateTime.now()).toMillis();
		this.repeat = cron.retrieveFieldsAsMap().values().stream().anyMatch(cronField -> cronField.getExpression() instanceof Every);

		// TODO absolute from NOW
		// TODO use complete cron info
/*
		Map<CronFieldName, CronField> fieldMap = cron.retrieveFieldsAsMap();
		fieldMap.forEach((key, value) -> {
			switchType(value.getExpression(),
					caze(Every.class, every -> {
						switch (key) {
							case SECOND:
								int delayMillis = every.getPeriod().getValue() * 1000;
								ScheduledMethod scheduledMethod = new ScheduledMethod(type, clazz, method, delayMillis);
								scheduledMethods.add(scheduledMethod);
								break;
							case MINUTE:
								break;
							case HOUR:
								break;
							case DAY_OF_MONTH:
								break;
							case MONTH:
								break;
							case DAY_OF_WEEK:
								break;
							case YEAR:
								break;
						}
					})
					// TODO: caze(Always.class, always -> {}),
					// TODO: caze(QuestionMark.class, questionMark -> {})
			);
		});
*/
	}

	@Override
	public void reset() {
		this.startTime = System.currentTimeMillis() + ExecutionTime.forCron(cron).timeToNextExecution(ZonedDateTime.now()).toMillis();
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(startTime, TimeUnit.MILLISECONDS);
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