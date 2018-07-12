package pro.landlabs.transaction.stats.test;

import org.joda.time.DateTime;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.util.Random;

public class TransactionMother {

    public static final double NUMBER_PRECISION = 0.001;
    public static final int AMOUNT_MAX = 1_000;

    public static Transaction createTransaction(DateTime dateTime) {
        return createTransaction(dateTime, randomAmount());
    }

    public static Transaction createTransaction(DateTime dateTime, double amount) {
        return new Transaction(amount, dateTime.getMillis());
    }

    static double randomAmount() {
        Random random = new Random();
        int intAmount = random.nextInt(AMOUNT_MAX);
        int intFraction = random.nextInt(1_000);

        return intAmount + intFraction * NUMBER_PRECISION;
    }

}
