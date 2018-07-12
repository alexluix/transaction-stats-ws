package pro.landlabs.transaction.stats.test;

import org.joda.time.DateTime;
import pro.landlabs.transaction.stats.ws.value.Transaction;

import java.util.Random;

public class TransactionMother {

    static final double NUMBER_PRECISION = 0.0001;
    static final int AMOUNT_MAX = 1_000;

    public static Transaction createTransaction(DateTime dateTime) {
        return createTransaction(dateTime, randomAmount());
    }

    public static Transaction createTransaction(DateTime dateTime, double amount) {
        return new Transaction(amount, dateTime.getMillis());
    }

    static double randomAmount() {
        Random random = new Random();
        int intAmount = random.nextInt(AMOUNT_MAX);
        int intFraction = random.nextInt(AMOUNT_MAX);

        return intAmount + intFraction * NUMBER_PRECISION;
    }

}
