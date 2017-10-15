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
public class AccountStorage
{
    private Map<UUID, UserAccount> accounts = new HashMap<>();

    /**
     * Add user account.
     *
     * @param account
     */
    public void addAccount(UserAccount account)
    {
        accounts.put(account.getId(), account);
    }

    /**
     * Delete user account.
     *
     * @param userId
     */
    public void deleteAccount(UUID userId)
    {
        accounts.remove(userId);
    }

    /**
     * Get the collection of all user accounts.
     *
     * @return collection of all user accounts
     */
    public Collection<UserAccount> getAllUserAccounts()
    {
        return accounts.values();
    }
}
