package ru.khasanov.rest.storage;

import ru.khasanov.rest.model.UserAccount;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory storage for user accounts.
 *
 * @author Aleksandr Khasanov
 */
public class AccountStorage {
    private Map<UUID, UserAccount> accounts = new HashMap<>();

    /**
     * Add user account.
     *
     * @param account user account. Must not be {@code null}
     */
    public void addAccount(UserAccount account) {
        accounts.put(account.getUserId(), account);
    }

    /**
     * Delete user account.
     *
     * @param userId user id. Must not be {@code null}
     * @return {@code true} if account was successfully deleted. {@code false} otherwise.
     */
    public boolean deleteAccount(UUID userId) {
        return accounts.remove(userId) != null;
    }

    /**
     * Get user account by id.
     *
     * @param userId user id. Must be not {@code null}
     * @return {@link UserAccount} for passed id if present. {@code null} otherwise.
     */
    public UserAccount getUserAccount(UUID userId) {
        return accounts.get(userId);
    }

    /**
     * Get the collection of all user accounts.
     *
     * @return collection of all user accounts
     */
    public Collection<UserAccount> getAllUserAccounts() {
        return accounts.values();
    }
}
