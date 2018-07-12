package pro.landlabs.transaction.stats.service.aggregation;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.junit.Test;
import pro.landlabs.transaction.stats.test.MockEpochSecondsProvider;
import pro.landlabs.transaction.stats.test.TransactionMother;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TimelineAggregatorTest {

    private static final int LAST_SECONDS = 5;
    private static final double DOUBLE_PRECISION = 0.0001;

    private MockEpochSecondsProvider mockEpochSecondsProvider = new MockEpochSecondsProvider();

    @Test
    public void shouldNotAggregateOutOfScopeFromThePast() {
        TimelineAggregator subject = new TimelineAggregator(LAST_SECONDS, mockEpochSecondsProvider);

        DateTime dateTime = mockEpochSecondsProvider.getDateTime();
        Transaction transaction1 = TransactionMother.createTransaction(dateTime.minusSeconds(LAST_SECONDS + 1));

        assertThat(subject.aggregate(transaction1), is(false));
        assertThat(subject.get(), nullValue());
    }

    @Test
    public void shouldNotAggregateOutOfScopeFromTheFuture() {
        TimelineAggregator subject = new TimelineAggregator(LAST_SECONDS, mockEpochSecondsProvider);

        DateTime dateTime = mockEpochSecondsProvider.getDateTime();
        Transaction transaction1 = TransactionMother.createTransaction(dateTime.plusSeconds(1));

        assertThat(subject.aggregate(transaction1), is(false));
        assertThat(subject.get(), nullValue());
    }

    @Test
    public void shouldAggregateFromThePastAndExcludeStaleData() {
        // given
        TimelineAggregator subject = new TimelineAggregator(LAST_SECONDS, mockEpochSecondsProvider);
        assertThat(subject.get(), nullValue());

        DateTime now = mockEpochSecondsProvider.getDateTime();
        Transaction transaction1 = TransactionMother.createTransaction(now.minusSeconds(1));
        Transaction transaction2 = TransactionMother.createTransaction(now.minusSeconds(2));
        Transaction transaction3 = TransactionMother.createTransaction(now.minusSeconds(3));
        Transaction outOfScope1 = TransactionMother.createTransaction(now.minusSeconds(4));
        Transaction outOfScope2 = TransactionMother.createTransaction(now.minusSeconds(5));

        assertThat(subject.aggregate(transaction1), is(true));
        assertThat(subject.aggregate(transaction2), is(true));
        assertThat(subject.aggregate(transaction3), is(true));
        assertThat(subject.aggregate(outOfScope1), is(true));
        assertThat(subject.aggregate(outOfScope2), is(true));

        // when then
        assertThat(subject.get().getCount(), equalTo(5L));
        mockEpochSecondsProvider.plusSecond();
        assertThat(subject.get().getCount(), equalTo(4L));
        mockEpochSecondsProvider.plusSecond();
        assertThat(subject.get().getCount(), equalTo(3L));
        mockEpochSecondsProvider.plusSecond();
        assertThat(subject.get().getCount(), equalTo(2L));
        mockEpochSecondsProvider.plusSecond();
        assertThat(subject.get().getCount(), equalTo(1L));
        mockEpochSecondsProvider.plusSecond();
        assertThat(subject.get(), nullValue());
    }

    @Test
    public void shouldAggregateTransactionWithSameTimestamp() {
        // given
        TimelineAggregator subject = new TimelineAggregator(LAST_SECONDS, mockEpochSecondsProvider);

        DateTime now = mockEpochSecondsProvider.getDateTime();
        Transaction transaction = TransactionMother.createTransaction(now.minusSeconds(5));

        assertThat(subject.aggregate(transaction), is(true));
        assertThat(subject.aggregate(transaction), is(true));
        assertThat(subject.aggregate(transaction), is(true));

        // when then
        assertThat(subject.get().getCount(), equalTo(3L));
        mockEpochSecondsProvider.plusSecond();
        assertThat(subject.get(), nullValue());
    }

    @Test
    public void shouldCalcStatsCorrectly() {
        TimelineAggregator subject = new TimelineAggregator(LAST_SECONDS, mockEpochSecondsProvider);

        DateTime dateTime = mockEpochSecondsProvider.getDateTime();
        int min = 50;
        int avg = 100;
        int max = 150;
        int sum = min + avg + max;
        List<Transaction> transactions = ImmutableList.of(
                TransactionMother.createTransaction(dateTime, min),
                TransactionMother.createTransaction(dateTime, avg),
                TransactionMother.createTransaction(dateTime, max)
        );
        for (Transaction transaction : transactions) {
            assertThat(subject.aggregate(transaction), is(true));
        }

        StatisticsRecord statisticsRecord = subject.get();

        assertThat(statisticsRecord.getSum(), closeTo(sum, DOUBLE_PRECISION));
        assertThat(statisticsRecord.getAvg(), closeTo(avg, DOUBLE_PRECISION));
        assertThat(statisticsRecord.getMax(), closeTo(max, DOUBLE_PRECISION));
        assertThat(statisticsRecord.getMin(), closeTo(min, DOUBLE_PRECISION));
    }

}
