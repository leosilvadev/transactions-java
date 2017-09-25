package com.github.leosilva.transactions;

import com.github.leosilvadev.transactions.domains.Transaction;
import com.github.leosilvadev.transactions.domains.TransactionsSummary;
import com.github.leosilvadev.transactions.repositories.TransactionRepository;
import com.github.leosilvadev.transactions.services.TransactionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leonardo on 9/25/17.
 */
public class TransactionManagerTest {

    private TransactionManager manager;

    @Before
    public void setup() {
        this.manager = new TransactionManager(new TransactionRepository());
        this.manager.setDefaultThresholdSeconds(60L);
    }

    @Test
    public void shouldAddSomeTransactionsAndGenerateASummaryWithASingleThread() {
        manager.register(new Transaction(10.0, Instant.now()));
        manager.register(new Transaction(11.0, Instant.now()));
        manager.register(new Transaction(12.0, Instant.now().plusSeconds(1)));
        manager.register(new Transaction(13.0, Instant.now().plusSeconds(1)));
        manager.register(new Transaction(14.0, Instant.now().plusSeconds(2)));
        manager.register(new Transaction(15.0, Instant.now().plusSeconds(2)));

        TransactionsSummary summary = manager.getSummary();

        Assert.assertTrue(doublesAreEqual(10.0, summary.getMin()));
        Assert.assertTrue(doublesAreEqual(15.0, summary.getMax()));
        Assert.assertTrue(doublesAreEqual(75.0, summary.getSum()));
        Assert.assertTrue(doublesAreEqual(12.5, summary.getAvg()));
        Assert.assertEquals(6L, summary.getCount());
    }


    @Test
    public void shouldAddSomeTransactionsAndGenerateASummaryWithASingleThreadAndOldTransactions() throws InterruptedException {
        manager.register(new Transaction(10.0, Instant.now()));
        manager.register(new Transaction(11.0, Instant.now()));
        manager.register(new Transaction(12.0, Instant.now().plusSeconds(1)));
        manager.register(new Transaction(13.0, Instant.now().plusSeconds(1)));
        manager.register(new Transaction(14.0, Instant.now().plusSeconds(2)));
        manager.register(new Transaction(15.0, Instant.now().plusSeconds(2)));

        manager.register(new Transaction(12.0, Instant.now().minusSeconds(58)));
        manager.register(new Transaction(13.0, Instant.now().minusSeconds(58)));
        manager.register(new Transaction(14.0, Instant.now().minusSeconds(58)));
        manager.register(new Transaction(15.0, Instant.now().minusSeconds(58)));

        Thread.sleep(5_000);
        TransactionsSummary summary = manager.getSummary();

        Assert.assertTrue(doublesAreEqual(10.0, summary.getMin()));
        Assert.assertTrue(doublesAreEqual(15.0, summary.getMax()));
        Assert.assertTrue(doublesAreEqual(75.0, summary.getSum()));
        Assert.assertTrue(doublesAreEqual(12.5, summary.getAvg()));
        Assert.assertEquals(6L, summary.getCount());
    }

    @Test
    public void shouldAddTransactionsWithMultipleThreadsAndGenerateASummary() throws InterruptedException {
        CountDownLatch counter = new CountDownLatch(5);

        buildThread(5.0, counter).start();
        buildThread(6.0, counter).start();
        buildThread(7.0, counter).start();
        buildThread(8.0, counter).start();
        buildThread(9.0, counter).start();

        buildThread(10.0, counter).start();
        buildThread(11.0, counter).start();
        buildThread(12.0, counter).start();
        buildThread(13.0, counter).start();
        buildThread(14.0, counter).start();

        counter.await();

        // Waiting aditional time, since the Queue can take longer to insert the objects
        Thread.sleep(5_000);

        TransactionsSummary summary = manager.getSummary();

        Assert.assertTrue(doublesAreEqual(5.0, summary.getMin()));
        Assert.assertTrue(doublesAreEqual(14.0, summary.getMax()));
        Assert.assertTrue(doublesAreEqual(95_000, summary.getSum()));
        Assert.assertTrue(doublesAreEqual(9.5, summary.getAvg()));
        Assert.assertEquals(10_000L, summary.getCount());
    }

    private static boolean doublesAreEqual(double value1, double value2) {
        return Double.doubleToLongBits(value1) == Double.doubleToLongBits(value2);
    }

    private Thread buildThread(final Double value, final CountDownLatch countDownLatch) {
        final AtomicLong threadCounter = new AtomicLong(0);
        return new Thread(() -> {
            while (threadCounter.incrementAndGet() <= 1000) {
                manager.register(new Transaction(value, Instant.now()));
            }
            countDownLatch.countDown();
        });
    }
}
