package com.github.leosilvadev.transactions.domains;

import java.util.Collection;

/**
 * Created by leonardo on 9/25/17.
 */
public class TransactionsSummary {

    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final long count;

    public TransactionsSummary(final Collection<Transaction> transactions) {
        this.sum = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        this.avg = transactions.stream().mapToDouble(Transaction::getAmount).average().orElseGet(this::defaultValue);
        this.max = transactions.stream().mapToDouble(Transaction::getAmount).max().orElseGet(this::defaultValue);
        this.min = transactions.stream().mapToDouble(Transaction::getAmount).min().orElseGet(this::defaultValue);
        this.count = (long) transactions.size();
    }

    public double defaultValue() {
        return 0.0;
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

}
