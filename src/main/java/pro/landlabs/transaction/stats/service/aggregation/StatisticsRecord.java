package pro.landlabs.transaction.stats.service.aggregation;

import com.google.common.base.MoreObjects;

public class StatisticsRecord {

    private final long id;
    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final long count;

    public StatisticsRecord(long id, double sum, double avg, double max, double min, long count) {
        this.id = id;
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public static StatisticsRecord create(long id, double number) {
        return new StatisticsRecord(id, number, number, number, number, 1);
    }

    public StatisticsRecord merge(double number) {
        long count = this.count + 1;

        double sum = this.sum + number;
        double min = Math.min(this.min, number);
        double max = Math.max(this.max, number);
        double avg = sum / count;

        return new StatisticsRecord(id, sum, avg, max, min, count);
    }

    public long getId() {
        return id;
    }

    public double getSum() {
        return sum;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("sum", sum)
                .add("avg", avg)
                .add("max", max)
                .add("min", min)
                .add("count", count)
                .toString();
    }
}
