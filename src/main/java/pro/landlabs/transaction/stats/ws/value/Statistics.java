package pro.landlabs.transaction.stats.ws.value;

import java.math.BigDecimal;

public class Statistics {
    private final BigDecimal sum;
    private final BigDecimal avg;
    private final BigDecimal max;
    private final BigDecimal min;
    private final long count;

    public Statistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

}
