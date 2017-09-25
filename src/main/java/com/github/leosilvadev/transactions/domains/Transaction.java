package com.github.leosilvadev.transactions.domains;

import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Created by leonardo on 9/25/17.
 */
public class Transaction {

    @NotNull
    private Double amount;

    @NotNull
    private Instant timestamp;

    public Transaction() {}

    public Transaction(final Double amount, final Instant timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
