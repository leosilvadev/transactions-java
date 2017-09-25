package com.github.leosilvadev.transactions.repositories;

import com.github.leosilvadev.transactions.domains.Transaction;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by leonardo on 9/25/17.
 */
@Repository
public class TransactionRepository {

    private final Queue<Transaction> transactions;

    public TransactionRepository() {
        this.transactions = new LinkedBlockingQueue<>();
    }

    public Transaction register(final Transaction transaction) {
        transactions.add(transaction);
        return transaction;
    }

    public void removeAllOlderThan(final Instant threshold) {
        transactions.stream()
                .filter(transaction -> transaction.getTimestamp().isBefore(threshold))
                .forEach(transactions::remove);
    }

    public List<Transaction> getAll() {
        return new ArrayList<>(transactions);
    }
}
