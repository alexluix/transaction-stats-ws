package pro.landlabs.transaction.stats.ws.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
    private final double amount;
    private final long timestamp;

    @JsonCreator
    public Transaction(
            @JsonProperty(value = "amount", required = true) double amount,
            @JsonProperty(value = "timestamp", required = true) long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
