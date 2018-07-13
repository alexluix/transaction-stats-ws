package pro.landlabs.transaction.stats.service;

import pro.landlabs.transaction.stats.service.aggregation.StatisticsRecord;
import pro.landlabs.transaction.stats.service.aggregation.TimelineAggregator;
import pro.landlabs.transaction.stats.ws.value.Statistics;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionStatisticsService {

    public static final int PRECISION_SCALE = 3;

    private final TimelineAggregator timelineAggregator;

    public TransactionStatisticsService(TimelineAggregator timelineAggregator) {
        this.timelineAggregator = timelineAggregator;
    }

    public boolean register(Transaction transaction) {
        return timelineAggregator.aggregate(transaction);
    }

    public Statistics getStatistics() {
        StatisticsRecord statisticsRecord = timelineAggregator.get();

        if (statisticsRecord == null) {
            return new Statistics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
        } else {
            BigDecimal sum = toDecimal(statisticsRecord.getSum());
            BigDecimal avg = toDecimal(statisticsRecord.getAvg());
            BigDecimal max = toDecimal(statisticsRecord.getMax());
            BigDecimal min = toDecimal(statisticsRecord.getMin());
            return new Statistics(sum, avg, max, min, statisticsRecord.getCount());
        }
    }

    private BigDecimal toDecimal(double number) {
        return new BigDecimal(Double.valueOf(number).toString())
                .setScale(PRECISION_SCALE, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

}
