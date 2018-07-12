package pro.landlabs.transaction.stats.service;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import pro.landlabs.transaction.stats.test.TransactionMother;
import pro.landlabs.transaction.stats.ws.value.Statistics;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
        assertThat(statistics.getSum().compareTo(BigDecimal.ZERO), equalTo(0));
        assertThat(statistics.getAvg().compareTo(BigDecimal.ZERO), equalTo(0));
        assertThat(statistics.getMax().compareTo(BigDecimal.ZERO), equalTo(0));
        assertThat(statistics.getMin().compareTo(BigDecimal.ZERO), equalTo(0));
        assertThat(statistics.getCount(), equalTo(0L));
    }

    @Test
    public void shouldCalcStatsForSingleTransaction() {
        // given
        Transaction transaction = TransactionMother.createTransaction(DateTime.now(), 12.3);
        subject.register(transaction);

        // when
        Statistics statistics = subject.getStatistics();

        // then
        assertThat(statistics.getSum().compareTo(toDecimal(transaction.getAmount())), equalTo(0));
        assertThat(statistics.getAvg().compareTo(toDecimal(transaction.getAmount())), equalTo(0));
        assertThat(statistics.getMax().compareTo(toDecimal(transaction.getAmount())), equalTo(0));
        assertThat(statistics.getMin().compareTo(toDecimal(transaction.getAmount())), equalTo(0));
        assertThat(statistics.getCount(), equalTo(1L));
    }

    @Test
    public void shouldCalcStatsForMultipleTransactions() {
        // given
        int min = 50;
        int avg = 100;
        int max = 150;
        int sum = min + avg + max;
        List<Transaction> transactions = ImmutableList.of(
                TransactionMother.createTransaction(DateTime.now(), min),
                TransactionMother.createTransaction(DateTime.now(), avg),
                TransactionMother.createTransaction(DateTime.now(), max)
        );
        transactions.forEach(transaction -> subject.register(transaction));

        // when
        Statistics statistics = subject.getStatistics();

        // then
        assertThat(statistics.getSum().compareTo(toDecimal(sum)), equalTo(0));
        assertThat(statistics.getAvg().compareTo(toDecimal(avg)), equalTo(0));
        assertThat(statistics.getMax().compareTo(toDecimal(max)), equalTo(0));
        assertThat(statistics.getMin().compareTo(toDecimal(min)), equalTo(0));
        assertThat(statistics.getCount(), equalTo((long) transactions.size()));
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

        assertThat(statistics.getSum().compareTo(toDecimal(summaryStatistics.getSum())), equalTo(0));
        assertThat(statistics.getAvg().compareTo(toDecimal(summaryStatistics.getAverage())), equalTo(0));
        assertThat(statistics.getMax().compareTo(toDecimal(summaryStatistics.getMax())), equalTo(0));
        assertThat(statistics.getMin().compareTo(toDecimal(summaryStatistics.getMin())), equalTo(0));
        assertThat(statistics.getCount(), equalTo((long) transactions.size()));
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
        assertThat(statistics.getCount(), equalTo(1L));
    }

    private BigDecimal toDecimal(Double number) {
        return new BigDecimal(number.toString()).setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal toDecimal(Integer number) {
        return new BigDecimal(number);
    }

}
