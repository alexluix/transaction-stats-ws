package pro.landlabs.transaction.stats.test;

import org.joda.time.DateTime;
import pro.landlabs.transaction.stats.service.aggregation.EpochSecondsProvider;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MockEpochSecondsProvider implements EpochSecondsProvider {

    private long epochSecond;

    public MockEpochSecondsProvider() {
        this.epochSecond = Instant.now().getEpochSecond();
    }

    @Override
    public long getEpochSecond() {
        return epochSecond;
    }

    public DateTime getDateTime() {
        return new DateTime(TimeUnit.SECONDS.toMillis(epochSecond));
    }

    public void plusSecond() {
        this.epochSecond++;
    }

}
