package pro.landlabs.transaction.stats.service.aggregation;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RingBuffer<T> {

    private final Object[] array;

    private int offset;

    public RingBuffer(int size) {
        this.array = new Object[size];
        this.offset = 0;
    }

    public void shift(int offset) {
        this.offset = (this.offset + offset) % array.length;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0) throw new IllegalArgumentException("index < 0");

        return (T) array[offsetIndex(index)];
    }

    public void set(int index, T value) {
        if (index < 0) throw new IllegalArgumentException("index < 0");

        array[offsetIndex(index)] = value;
    }

    private int offsetIndex(int index) {
        return (offset + index) % array.length;
    }

}
