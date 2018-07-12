package pro.landlabs.transaction.stats.service.aggregation;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StatisticsRecordTest {

    @Test
    public void shouldCreateNewStatistics() {
        long id = 7;
        double number = 500;

        StatisticsRecord record = StatisticsRecord.create(id, number);

        assertThat(record.getCount(), equalTo(1L));
        assertThat(record.getSum(), equalTo(number));
        assertThat(record.getAvg(), equalTo(number));
        assertThat(record.getMin(), equalTo(number));
        assertThat(record.getMax(), equalTo(number));
    }

    @Test
    public void shouldMerge() {
        long id = 9;
        StatisticsRecord statisticsRecord =
                new StatisticsRecord(id, 300, 100, 150, 50, 3L);

        StatisticsRecord merged = statisticsRecord.merge(500);

        assertThat(merged.getId(), equalTo(id));
        assertThat(merged.getSum(), equalTo(800D));
        assertThat(merged.getMax(), equalTo(500D));
        assertThat(merged.getMin(), equalTo(50D));
        assertThat(merged.getAvg(), equalTo(200D));
    }

}
