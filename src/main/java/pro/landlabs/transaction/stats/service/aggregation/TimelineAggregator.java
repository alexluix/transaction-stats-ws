package pro.landlabs.transaction.stats.service.aggregation;

import org.joda.time.DateTime;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimelineAggregator {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final EpochSecondsProvider epochSecondsProvider;
    private final int lastSeconds;
    private final long startedEpochSecond;
    private final RingBuffer<StatisticsRecord> ringBuffer;

    private int bufferOffset = 0;

    public TimelineAggregator(int lastSeconds, EpochSecondsProvider epochSecondsProvider) {
        this.epochSecondsProvider = epochSecondsProvider;
        this.lastSeconds = lastSeconds;
        this.startedEpochSecond = currentEpochSecond();
        this.ringBuffer = new RingBuffer<>(lastSeconds + 1);
    }

    private long currentEpochSecond() {
        return epochSecondsProvider.getEpochSecond();
    }

    public StatisticsRecord get() {
        long currentEpochSecond = currentEpochSecond();

        readWriteLock.readLock().lock();
        int shift = (int) (currentEpochSecond - startedEpochSecond - bufferOffset);
        StatisticsRecord value = ringBuffer.get(shift);
        readWriteLock.readLock().unlock();

        return value != null && value.getId() == currentEpochSecond - lastSeconds ? value : null;
    }

    public boolean aggregate(Transaction transaction) {
        long currentEpochSecond = currentEpochSecond();
        long transactionEpochSecond = toEpochSecond(transaction.getTimestamp());

        int transactionSecond = (int) (currentEpochSecond - transactionEpochSecond);
        if (transactionSecond < 0 || transactionSecond > lastSeconds) {
            return false;
        }

        readWriteLock.writeLock().lock();

        int shift = (int) (currentEpochSecond - startedEpochSecond - bufferOffset);
        if (shift > 0) {
            bufferOffset += shift;
            ringBuffer.shift(shift);
        }

        for (int i = 0; i <= lastSeconds - transactionSecond; i++) {
            StatisticsRecord record = ringBuffer.get(i);

            long trnEpochSecond = startedEpochSecond + bufferOffset - lastSeconds + i;
            ringBuffer.set(i, createOrUpdate(record, trnEpochSecond, transaction.getAmount()));
        }

        readWriteLock.writeLock().unlock();

        return true;
    }

    private StatisticsRecord createOrUpdate(StatisticsRecord record, long id, double number) {
        if (record == null || record.getId() != id) {
            return StatisticsRecord.create(id, number);
        } else {
            return record.merge(number);
        }
    }

    private static long toEpochSecond(long timestamp) {
        long millis = new DateTime(timestamp).withMillisOfSecond(0).getMillis();
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

}
