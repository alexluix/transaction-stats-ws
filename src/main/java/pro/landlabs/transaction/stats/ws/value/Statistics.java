package pro.landlabs.transaction.stats.ws.value;

public class Statistics {
    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final double count;

    public Statistics(double sum, double avg, double max, double min, double count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
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

    public double getCount() {
        return count;
    }
}
