package pro.landlabs.transaction.stats.service;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import pro.landlabs.transaction.stats.ws.value.Statistics;
import pro.landlabs.transaction.stats.ws.value.Transaction;

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

        double max = stats.getMax() == Double.NEGATIVE_INFINITY ? 0 : stats.getMax();
        double min = stats.getMin() == Double.POSITIVE_INFINITY ? 0 : stats.getMin();

        return new Statistics(
                stats.getSum(), stats.getAverage(), max, min, stats.getCount());
    }

}
