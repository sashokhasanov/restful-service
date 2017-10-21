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

    private static final int TIMEOUT_MILLISECONDS = 1000;

    private AccountStorage accountStorage;

    private ExecutorService executorService;

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
     * Create new user account.
     */
    public void createNewAccount() {
        try {
            executorService.submit(() ->
                    accountStorage.addAccount(new UserAccount())).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create new user account.
     *
     * @param userId uiser id. Must not be {@code null}
     * @param balance user balance. Must not be {@code null}
     */
    public void createNewAccount(UUID userId, BigDecimal balance) {
        try {
            executorService.submit(() ->
                    accountStorage.addAccount(new UserAccount(userId, balance))).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete user account by id.
     *
     * @param userId user id. Must not be {@code null}
     */
    public void deleteAccount(UUID userId) {
        try {
            executorService.submit(() ->
                    accountStorage.deleteAccount(userId)).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find user account by id.
     *
     * @param userId user id. Must not be {@code null}
     * @return {@link UserAccount} if found. {@code null} otherwise
     */
    public UserAccount find(UUID userId) {
        try {
            return executorService.submit(() ->
                    accountStorage.getUserAccount(userId)).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
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
     * Get collection of all user accounts.
     *
     * @return collection of all user accounts
     */
    public Collection<UserAccount> getAllAccounts() {
        try {
            return executorService.submit(() ->
                    accountStorage.getAllUserAccounts()).get(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}
