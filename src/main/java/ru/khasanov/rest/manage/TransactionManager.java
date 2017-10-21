package ru.khasanov.rest.manage;

import ru.khasanov.rest.model.TransferTransaction;
import ru.khasanov.rest.model.UserAccount;
import ru.khasanov.rest.storage.AccountStorage;
import ru.khasanov.rest.storage.TransactionStorage;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Class that is used to manage transfer transactions.
 *
 * @author Aleksandr Khasanov
 */
public class TransactionManager {

    private static final int TIMEOUT_MILLISECONDS = 3000;

    private TransactionStorage transactionStorage;

    private AccountStorage accountStorage;

    private ExecutorService executorService;

    /**
     * Creates new instance of {@link TransactionManager}
     *
     * @param transactionStorage {@link TransactionStorage}. Must not be {@code null}
     * @param accountStorage {@link AccountStorage}. Must not be {@code null}
     * @param executorService {@link ExecutorService}. Must not be {@code null}
     */
    public TransactionManager(TransactionStorage transactionStorage, AccountStorage accountStorage, ExecutorService executorService) {
        this.transactionStorage = transactionStorage;
        this.accountStorage = accountStorage;
        this.executorService = executorService;
    }

    /**
     * Transfer amount from transmitter to recipient.
     *
     * @param fromId transmitter id. Must not be {@code null}
     * @param toId recipient id. Must not be {@code null}
     * @param amount amount of money to transfer. Must not be {@code null}
     */
    public void transfer(UUID fromId, UUID toId, BigDecimal amount)
    {
        try {
            executorService.submit(new TransferTask(fromId, toId, amount)).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all transactions.
     *
     * @return list of all transactions.
     */
    public List<TransferTransaction> geAllTransactions()
    {
        try {
            return executorService.submit(() -> transactionStorage.getAlTransactions()).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get list of transactions for specific transmitter.
     *
     * @param fromId transmitter id. Must not be {@code null}
     * @return list of transactions for specified transmitter.
     */
    public List<TransferTransaction> getFromTransactions(UUID fromId)
    {
        try {
            return executorService.submit(() -> transactionStorage.getFromTransactions(fromId)).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all transactions for specific recipient.
     *
     * @param toId recipient id. Must not be {@code null}
     * @return list of transactions for specified recipient
     */
    public List<TransferTransaction> getToTransactions(UUID toId)
    {
        try {
            return executorService.submit(() -> transactionStorage.getFromTransactions(toId)).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class TransferTask implements Runnable
    {
        private UUID fromId;

        private UUID toId;

        private BigDecimal amount;

        public TransferTask(UUID fromId, UUID toId, BigDecimal amount)
        {
            this.fromId = fromId;
            this.toId = toId;
            this.amount = amount;
        }

        @Override
        public void run()
        {
            UserAccount fromAccount = accountStorage.getUserAccount(fromId);
            UserAccount toAccount = accountStorage.getUserAccount(toId);

            if (fromAccount == null || toAccount == null)
            {
                throw new IllegalArgumentException("No such user");
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new IllegalArgumentException("Attempting to transfer negative amount: " + amount);
            }

            if (fromAccount.getBalance().compareTo(amount) < 0)
            {
                throw new IllegalArgumentException("Not enought money to transfer");
            }

            TransferTransaction transaction = new TransferTransaction(fromId, toId, amount, OffsetDateTime.now());
            transactionStorage.addTransaction(transaction);
            fromAccount.withdraw(amount);
            toAccount.acquire(amount);
        }
    }
}