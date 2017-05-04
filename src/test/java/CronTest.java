import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.CronField;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.field.expression.Always;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.field.expression.QuestionMark;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;

import static utils.Util.caze;
import static utils.Util.switchType;

class CronTest {

    private CronParser parser;

    @BeforeEach
    void setUp() {
        parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
    }

    @Test
    void executionTime() {
        Cron cron = parser.parse("0/10 * * * * ?");
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        Duration duration = executionTime.timeToNextExecution(ZonedDateTime.now());
        System.out.println("duration.toMillis() = " + duration.toMillis());
    }

    @Test
    void expressionValue() {
        Cron cron = parser.parse("0/10 0/2 * * * ?");
        Map<CronFieldName, CronField> fieldMap = cron.retrieveFieldsAsMap();
        fieldMap.forEach((key, value) -> {
            String namePrefix = key + " ";
            switchType(value.getExpression(),
                    caze(Every.class, every -> {
                        System.out.println(namePrefix + every.getPeriod());
                    }),
                    caze(Always.class, always -> System.out.println(namePrefix + always.asString())),
                    caze(QuestionMark.class, questionMark -> System.out.println(namePrefix + questionMark.asString()))
            );
        });
    }
}