package com.github.leosilvadev.transactions.services;

import com.github.leosilvadev.transactions.domains.Transaction;
import com.github.leosilvadev.transactions.domains.TransactionsSummary;
import com.github.leosilvadev.transactions.exceptions.OutDatedTransaction;
import com.github.leosilvadev.transactions.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leonardo on 9/25/17.
 */
@Service
public class TransactionManager {

    private final TransactionRepository repository;
    public AtomicLong counter = new AtomicLong(0);

    @Value("${transactions.defaultThresholdSeconds}")
    private Long defaultThresholdSeconds;

    public TransactionManager(final TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction register(final Transaction transaction) {
        if (isOutDated(transaction)) {
            throw new OutDatedTransaction();
        }

        counter.incrementAndGet();
        return repository.register(transaction);
    }

    public TransactionsSummary getSummary() {
        final Instant threshold = Instant.now().minusSeconds(defaultThresholdSeconds);
        this.repository.removeAllOlderThan(threshold);
        return new TransactionsSummary(repository.getAll());
    }

    private Boolean isOutDated(final Transaction transaction) {
        return transaction.getTimestamp().isBefore(Instant.now().minusSeconds(defaultThresholdSeconds));
    }

    public void setDefaultThresholdSeconds(final Long defaultThresholdSeconds) {
        this.defaultThresholdSeconds = defaultThresholdSeconds;
    }
}
