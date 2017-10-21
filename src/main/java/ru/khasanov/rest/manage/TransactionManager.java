package ru.khasanov.rest.manage;

import ru.khasanov.rest.model.TransferTransaction;
import ru.khasanov.rest.model.UserAccount;
import ru.khasanov.rest.storage.AccountStorage;
import ru.khasanov.rest.storage.TransactionStorage;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class that is used to manage transfer transactions.
 *
 * @author Aleksandr Khasanov
 */
public class TransactionManager {

    private static final int DEFAULT_TIMEOUT = 1000;

    private TransactionStorage transactionStorage;

    private AccountStorage accountStorage;

    private ExecutorService executorService;

    private int timeout = DEFAULT_TIMEOUT;

    /**
     * Creates new instance of {@link TransactionManager}
     *
     * @param transactionStorage {@link TransactionStorage}. Must not be {@code null}
     * @param accountStorage     {@link AccountStorage}. Must not be {@code null}
     * @param executorService    {@link ExecutorService}. Must not be {@code null}
     */
    public TransactionManager(TransactionStorage transactionStorage, AccountStorage accountStorage, ExecutorService executorService) {
        this.transactionStorage = transactionStorage;
        this.accountStorage = accountStorage;
        this.executorService = executorService;
    }

    /**
     * Get timeout in milliseconds.
     *
     * @return timeout in milliseconds.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set up timeout.
     *
     * @param timeout timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Transfer amount from transmitter to recipient.
     *
     * @param fromId transmitter id. Must not be {@code null}
     * @param toId   recipient id. Must not be {@code null}
     * @param amount amount of money to transfer. Must not be {@code null}
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     * @throws TimeoutException     if the wait timed out
     */
    public void transfer(UUID fromId, UUID toId, BigDecimal amount) throws InterruptedException, ExecutionException, TimeoutException {
        executorService.submit(new TransferTask(fromId, toId, amount)).get(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Get all transactions.
     *
     * @return list of all transactions.
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     * @throws TimeoutException     if the wait timed out
     */
    public List<TransferTransaction> geAllTransactions() throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() ->
                transactionStorage.getAlTransactions()).get(timeout, TimeUnit.MILLISECONDS);

    }

    /**
     * Get list of transactions for specific transmitter.
     *
     * @param fromId transmitter id. Must not be {@code null}
     * @return list of transactions for specified transmitter
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     * @throws TimeoutException     if the wait timed out
     */
    public List<TransferTransaction> getFromTransactions(UUID fromId) throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() ->
                transactionStorage.getFromTransactions(fromId)).get(timeout, TimeUnit.MILLISECONDS);

    }

    /**
     * Get all transactions for specific recipient.
     *
     * @param toId recipient id. Must not be {@code null}
     * @return list of transactions for specified recipient
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     * @throws TimeoutException     if the wait timed out
     */
    public List<TransferTransaction> getToTransactions(UUID toId) throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() ->
                transactionStorage.getToTransactions(toId)).get(timeout, TimeUnit.MILLISECONDS);

    }

    /**
     * Get list of transactions for specific period of time.
     *
     * @param fromDate start point. Must not be {@code null}
     * @param toDate   end point. Must not be {@code null}
     * @return @link List} of transactions for specified period of time
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     * @throws TimeoutException     if the wait timed out
     */
    public List<TransferTransaction> getDateTimeTransactions(OffsetDateTime fromDate, OffsetDateTime toDate) throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.submit(() ->
                transactionStorage.getDateTimeTransactions(fromDate, toDate)).get(timeout, TimeUnit.MILLISECONDS);

    }

    private class TransferTask implements Runnable {
        private UUID fromId;

        private UUID toId;

        private BigDecimal amount;

        TransferTask(UUID fromId, UUID toId, BigDecimal amount) {
            this.fromId = fromId;
            this.toId = toId;
            this.amount = amount;
        }

        @Override
        public void run() {
            UserAccount fromAccount = accountStorage.getUserAccount(fromId);
            UserAccount toAccount = accountStorage.getUserAccount(toId);

            if (fromAccount == null || toAccount == null) {
                StringJoiner joiner = new StringJoiner(",", "Users with following ids do not exist: ", "");

                if (fromAccount == null) {
                    joiner.add(fromId.toString());
                }

                if (toAccount == null) {
                    joiner.add(toId.toString());
                }

                throw new IllegalArgumentException(joiner.toString());
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Attempting to transfer negative amount: " + amount);
            }

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Balance is too low");
            }

            TransferTransaction transaction = new TransferTransaction(fromId, toId, amount, OffsetDateTime.now());
            transactionStorage.addTransaction(transaction);
            fromAccount.withdraw(amount);
            toAccount.acquire(amount);
        }
    }
}
