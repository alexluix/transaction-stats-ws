package pro.landlabs.transaction.stats.service.aggregation;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class RingBufferTest {

    private RingBuffer<Integer> subject;

    private final int size = 5;

    @Before
    public void setUp() {
        subject = new RingBuffer<>(size);
    }

    @Test
    public void shouldSetAndGet() {
        int number = 7;

        subject.set(0, number);
        Integer actual = subject.get(0);

        assertThat(actual, equalTo(number));
    }

    @Test
    public void shouldSetAndGetSequence() {
        subject.set(0, 1);
        subject.set(1, 2);
        subject.set(2, 3);
        subject.set(3, 4);
        subject.set(4, 5);

        assertThat(subject.get(0), equalTo(1));
        assertThat(subject.get(1), equalTo(2));
        assertThat(subject.get(2), equalTo(3));
        assertThat(subject.get(3), equalTo(4));
        assertThat(subject.get(4), equalTo(5));
    }

    @Test
    public void shouldGetWithOverflow() {
        subject = new RingBuffer<>(3);
        subject.set(0, 1);
        subject.set(1, 2);
        subject.set(2, 3);

        assertThat(subject.get(0), equalTo(1));
        assertThat(subject.get(1), equalTo(2));
        assertThat(subject.get(2), equalTo(3));
        assertThat(subject.get(3), equalTo(1));
        assertThat(subject.get(4), equalTo(2));
        assertThat(subject.get(5), equalTo(3));
        assertThat(subject.get(6), equalTo(1));
    }

    @Test
    public void shouldSetWithOverflow() {
        subject = new RingBuffer<>(3);
        subject.set(0, 1);
        subject.set(1, 2);
        subject.set(2, 3);
        subject.set(5, 7);

        assertThat(subject.get(0), equalTo(1));
        assertThat(subject.get(1), equalTo(2));
        assertThat(subject.get(2), equalTo(7));
    }

    @Test
    public void shouldShiftAndGet() {
        subject.set(0, 1);
        subject.set(1, 2);
        subject.set(2, 3);
        subject.set(3, 4);
        subject.set(4, 5);
        assertThat(subject.get(0), equalTo(1));
        assertThat(subject.get(1), equalTo(2));

        subject.shift(1);

        assertThat(subject.get(0), equalTo(2));
        assertThat(subject.get(1), equalTo(3));
        assertThat(subject.get(2), equalTo(4));
        assertThat(subject.get(3), equalTo(5));
        assertThat(subject.get(4), equalTo(1));
    }

    @Test
    public void shouldShiftTwiceAndGet() {
        subject.set(0, 1);
        subject.set(1, 2);
        subject.set(2, 3);
        subject.set(3, 4);
        subject.set(4, 5);
        assertThat(subject.get(0), equalTo(1));
        assertThat(subject.get(1), equalTo(2));

        subject.shift(2);

        assertThat(subject.get(0), equalTo(3));
        assertThat(subject.get(1), equalTo(4));
        assertThat(subject.get(2), equalTo(5));
        assertThat(subject.get(3), equalTo(1));
        assertThat(subject.get(4), equalTo(2));
    }

    @Test
    public void shouldShiftMultipleCircles() {
        subject.set(0, 1);
        subject.set(1, 2);
        subject.set(2, 3);
        subject.set(3, 4);
        subject.set(4, 5);
        assertThat(subject.get(0), equalTo(1));
        assertThat(subject.get(1), equalTo(2));

        subject.shift(12);

        assertThat(subject.get(2), equalTo(5));
        assertThat(subject.get(3), equalTo(1));
        assertThat(subject.get(4), equalTo(2));
    }

    @Test
    public void shouldShiftAllRoundAndGet() {
        subject.set(0, 1);
        subject.set(1, 2);
        subject.set(2, 3);
        subject.set(3, 4);
        subject.set(4, 5);

        subject.shift(4);
        subject.shift(2);

        assertThat(subject.get(0), equalTo(2));
        assertThat(subject.get(1), equalTo(3));
        assertThat(subject.get(2), equalTo(4));
        assertThat(subject.get(3), equalTo(5));
        assertThat(subject.get(4), equalTo(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetOutOfRangeNegative() {
        subject.get(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSetOutOfRangeNegative() {
        subject.set(-1, 0);
    }

}
