package ru.khasanov.rest.manage;

import ru.khasanov.rest.model.UserAccount;
import ru.khasanov.rest.storage.AccountStorage;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class that is used to manage accounts storead in {@link AccountStorage}.
 *
 * @author Aleksandr Khasanov
 */
public class AccountManager {

    private static final int DEFAULT_TIMEOUT = 1000;

    private AccountStorage accountStorage;

    private ExecutorService executorService;

    private int timeout = DEFAULT_TIMEOUT;

    /**
     * Creates new instance of {@link AccountManager}.
     *
     * @param accountStorage  account storage. Must not be {@code null}
     * @param executorService executor. Must not be {@code null}
     */
    public AccountManager(AccountStorage accountStorage, ExecutorService executorService) {
        this.accountStorage = accountStorage;
        this.executorService = executorService;
    }

    /**
     * Get timeout in milliseconds.
     *
     * @return timeout in milliseconds
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
     * Create new user account.
     *
     * @return created {@link UserAccount}
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException     if the wait timed out
     */
    public UserAccount createNewAccount() throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() -> {
            UserAccount account = new UserAccount();
            accountStorage.addAccount(account);
            return account;
        }).get(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Create new user account.
     *
     * @param userId  user id. Must not be {@code null}
     * @param balance user balance. Must not be {@code null}
     * @return created {@link UserAccount}
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException     if the wait timed out
     */
    public UserAccount createNewAccount(UUID userId, BigDecimal balance) throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() -> {
            UserAccount account = new UserAccount(userId, balance);
            accountStorage.addAccount(account);
            return account;
        }).get(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Delete user account by id.
     *
     * @param userId user id. Must not be {@code null}
     * @return {@code true} if account was successfully deleted. {@code false} otherwise
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException     if the wait timed out
     */
    public boolean deleteAccount(UUID userId) throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() ->
                accountStorage.deleteAccount(userId)).get(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Get user account by id.
     *
     * @param userId user id. Must not be {@code null}
     * @return {@link UserAccount} if exists. {@code null} otherwise
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException     if the wait timed out
     */
    public UserAccount getAccount(UUID userId) throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() ->
                accountStorage.getUserAccount(userId)).get(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Get collection of all user accounts.
     *
     * @return collection of all user accounts
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException     if the wait timed out
     */
    public Collection<UserAccount> getAllAccounts() throws InterruptedException, ExecutionException, TimeoutException {

        return executorService.submit(() ->
                accountStorage.getAllUserAccounts()).get(timeout, TimeUnit.MILLISECONDS);

    }
}
