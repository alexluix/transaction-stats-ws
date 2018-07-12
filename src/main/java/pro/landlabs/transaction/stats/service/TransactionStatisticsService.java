package pro.landlabs.transaction.stats.service;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import pro.landlabs.transaction.stats.ws.value.Statistics;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionStatisticsService {

    private final int seconds;
    private final List<Transaction> transactions = Lists.newArrayList();

    public TransactionStatisticsService(int intervalSeconds) {
        seconds = intervalSeconds;
    }

    public void register(Transaction transaction) {
        transactions.add(transaction);
    }

    public Statistics getStatistics() {
        DoubleSummaryStatistics stats =
                transactions.stream().filter(transaction -> {
                    DateTime measureStart = DateTime.now().millisOfSecond().roundFloorCopy().minusSeconds(seconds);
                    return transaction.getTimestamp() > measureStart.getMillis();
                }).collect(Collectors.summarizingDouble(Transaction::getAmount));

        BigDecimal sum = toDecimal(stats.getSum());
        BigDecimal avg = toDecimal(stats.getAverage());
        BigDecimal max = toDecimal(stats.getMax() == Double.NEGATIVE_INFINITY ? 0 : stats.getMax());
        BigDecimal min = toDecimal(stats.getMin() == Double.POSITIVE_INFINITY ? 0 : stats.getMin());

        return new Statistics(sum, avg, max, min, stats.getCount());
    }

    private BigDecimal toDecimal(Double number) {
        return new BigDecimal(number.toString())
                .setScale(3, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

}
