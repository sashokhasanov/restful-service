package ru.khasanov.rest.manage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.khasanov.rest.model.TransferTransaction;
import ru.khasanov.rest.model.UserAccount;
import ru.khasanov.rest.storage.AccountStorage;
import ru.khasanov.rest.storage.TransactionStorage;

import javax.ws.rs.core.MultivaluedMap;
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

    private static Logger logger = LogManager.getLogger(TransactionManager.class);

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
     * <p>Get list of transactions that match specific query parameters.</p>
     * <p>Following parameters are accepted:</p>
     * <ul>
     * <li>from_id - specifies id of the transmitter. This parameter should match {@link UUID} string representation.</li>
     * <li>to_id - specifies id of the recipient. This parameter should match {@link UUID} string representation.</li>
     * <li>from_date - specifies beginning of time period. This parameter should match {@link OffsetDateTime} string representation.</li>
     * <li>to_date - specifies ending of time period. This parameter should match {@link OffsetDateTime} string representation.</li>
     * </ul>
     * <p>Parameters that are not supported are ignored while method execution.</p>
     *
     * @param queryParameters map of query parameters. Must not be {@code null}
     * @return {@link List} of transactions that match passed query parameters.
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     * @throws TimeoutException     if the wait timed out
     */
    public List<TransferTransaction> getTransactions(MultivaluedMap<String, String> queryParameters) throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.submit(() ->
                transactionStorage.getTransactions(queryParameters)).get(timeout, TimeUnit.MILLISECONDS);
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
                    logger.warn("Account with following id does not exist: {}", fromId);
                }

                if (toAccount == null) {
                    joiner.add(toId.toString());
                    logger.warn("Account with following id does not exist: {}", toId);
                }

                throw new IllegalArgumentException(joiner.toString());
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Attempting to transfer negative amount: {}", amount);
                throw new IllegalArgumentException("Attempting to transfer negative amount: " + amount);
            }

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                logger.warn(
                        "Balance of {} is {}, while attempting to transfer {}", fromId, fromAccount.getBalance(), amount);
                throw new IllegalArgumentException("Balance is too low");
            }

            TransferTransaction transaction = new TransferTransaction(fromId, toId, amount, OffsetDateTime.now());
            transactionStorage.addTransaction(transaction);
            fromAccount.withdraw(amount);
            toAccount.acquire(amount);
        }
    }
}
