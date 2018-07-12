package pro.landlabs.transaction.stats.service;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import pro.landlabs.transaction.stats.test.TransactionMother;
import pro.landlabs.transaction.stats.ws.value.Statistics;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static pro.landlabs.transaction.stats.test.TransactionMother.NUMBER_PRECISION;

public class TransactionStatisticsServiceTest {

    private static final int INTERVAL_SECONDS = 5;

    private TransactionStatisticsService subject;

    @Before
    public void setUp() {
        subject = new TransactionStatisticsService(INTERVAL_SECONDS);
    }

    @Test
    public void shouldReturnEmptyStatsWhenNoTransactionsRegistered() {
        // when
        Statistics statistics = subject.getStatistics();

        // then
        assertThat(statistics.getSum(), equalTo(0d));
        assertThat(statistics.getAvg(), equalTo(0d));
        assertThat(statistics.getMax(), equalTo(0d));
        assertThat(statistics.getMin(), equalTo(0d));
        assertThat(statistics.getCount(), equalTo(0d));
    }

    @Test
    public void shouldCalcStatsForASingleTransaction() {
        // given
        Transaction transaction = TransactionMother.createTransaction(DateTime.now(), 50.001);
        subject.register(transaction);

        // when
        Statistics statistics = subject.getStatistics();

        // then
        assertThat(statistics.getSum(), closeTo(transaction.getAmount(), NUMBER_PRECISION));
        assertThat(statistics.getAvg(), closeTo(transaction.getAmount(), NUMBER_PRECISION));
        assertThat(statistics.getMax(), closeTo(transaction.getAmount(), NUMBER_PRECISION));
        assertThat(statistics.getMin(), closeTo(transaction.getAmount(), NUMBER_PRECISION));
        assertThat(statistics.getCount(), equalTo(1d));
    }

    @Test
    public void shouldCalcStatsForMultipleTransactions() {
        // given
        double min = 50;
        double avg = 100;
        double max = 150;
        double sum = min + avg + max;
        List<Transaction> transactions = ImmutableList.of(
                TransactionMother.createTransaction(DateTime.now(), min),
                TransactionMother.createTransaction(DateTime.now(), avg),
                TransactionMother.createTransaction(DateTime.now(), max)
        );
        transactions.forEach(transaction -> subject.register(transaction));

        // when
        Statistics statistics = subject.getStatistics();

        // then
        assertThat(statistics.getSum(), closeTo(sum, NUMBER_PRECISION));
        assertThat(statistics.getAvg(), closeTo(avg, NUMBER_PRECISION));
        assertThat(statistics.getMax(), closeTo(max, NUMBER_PRECISION));
        assertThat(statistics.getMin(), closeTo(min, NUMBER_PRECISION));
        assertThat(statistics.getCount(), equalTo((double) transactions.size()));
    }

    @Test
    public void shouldCalcStatsForMultipleRandomTransactions() {
        // given
        List<Transaction> transactions = ImmutableList.of(
                TransactionMother.createTransaction(DateTime.now()),
                TransactionMother.createTransaction(DateTime.now()),
                TransactionMother.createTransaction(DateTime.now())
        );
        transactions.forEach(transaction -> subject.register(transaction));

        // when
        Statistics statistics = subject.getStatistics();

        // then
        DoubleSummaryStatistics summaryStatistics =
                transactions.stream().collect(Collectors.summarizingDouble(Transaction::getAmount));

        assertThat(statistics.getSum(), closeTo(summaryStatistics.getSum(), NUMBER_PRECISION));
        assertThat(statistics.getAvg(), closeTo(summaryStatistics.getAverage(), NUMBER_PRECISION));
        assertThat(statistics.getMax(), closeTo(summaryStatistics.getMax(), NUMBER_PRECISION));
        assertThat(statistics.getMin(), closeTo(summaryStatistics.getMin(), NUMBER_PRECISION));
        assertThat(statistics.getCount(), equalTo((double) transactions.size()));
    }

    @Test
    public void shouldNotConsiderOutOfTimeFrameTransactions() {
        // given
        DateTime outOfTimeFrame = DateTime.now().minusSeconds(INTERVAL_SECONDS + 1);
        List<Transaction> transactions = ImmutableList.of(
                TransactionMother.createTransaction(outOfTimeFrame),
                TransactionMother.createTransaction(outOfTimeFrame),
                TransactionMother.createTransaction(DateTime.now())
        );
        transactions.forEach(transaction -> subject.register(transaction));

        // when
        Statistics statistics = subject.getStatistics();

        // then
        assertThat(statistics.getCount(), equalTo(1d));
    }

}
