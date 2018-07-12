package pro.landlabs.transaction.stats.test;

import org.junit.Test;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class TransactionMotherTest {

    @Test
    public void shouldGenerateRandomAmounts() {
        double amount = TransactionMother.randomAmount();

        assertThat(amount, greaterThanOrEqualTo(0D));
        assertThat(amount, lessThanOrEqualTo((double) TransactionMother.AMOUNT_MAX));
    }

}
