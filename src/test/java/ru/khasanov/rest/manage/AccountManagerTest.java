package ru.khasanov.rest.manage;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import ru.khasanov.rest.model.UserAccount;
import ru.khasanov.rest.storage.AccountStorage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

/**
 * Tests of {@link AccountManager} class
 *
 * @author Aleksandr Khasanov
 */
public class AccountManagerTest {

    private AccountManager accountManager;

    @Before
    public void setUp() {
        AccountStorage storage = new AccountStorage();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        accountManager = new AccountManager(storage, executorService);
    }

    @Test
    public void testCreateAccount() throws InterruptedException, ExecutionException, TimeoutException {

        assumeThat(accountManager.getAllAccounts().size(), Is.is(0));

        UUID id = UUID.randomUUID();
        BigDecimal balance = BigDecimal.TEN;
        accountManager.createNewAccount(id, balance);

        List<UserAccount> accounts = accountManager.getAllAccounts();
        assertEquals(1, accounts.size());

        UserAccount actualAccount = accounts.get(0);
        assertEquals(id, actualAccount.getUserId());
        assertEquals(balance, actualAccount.getBalance());
    }

    @Test
    public void testGetExistingAccount() throws InterruptedException, ExecutionException, TimeoutException {

        assumeThat(accountManager.getAllAccounts().size(), Is.is(0));

        UUID id = UUID.randomUUID();
        BigDecimal balance = BigDecimal.TEN;
        UserAccount account = accountManager.createNewAccount(id, balance);

        List<UserAccount> accounts = accountManager.getAllAccounts();
        assumeThat(accounts.size(), Is.is(1));
        assumeThat(accounts.get(0), Is.is(account));

        UserAccount actualAccount = accountManager.getAccount(id);
        assertEquals(id, actualAccount.getUserId());
        assertEquals(balance, actualAccount.getBalance());
    }

    @Test
    public void testGetNotExistingAccount() throws InterruptedException, ExecutionException, TimeoutException {

        assumeThat(accountManager.getAllAccounts().size(), Is.is(0));

        UUID id = UUID.randomUUID();
        BigDecimal balance = BigDecimal.TEN;
        UserAccount account = accountManager.createNewAccount(id, balance);

        List<UserAccount> accounts = accountManager.getAllAccounts();
        assumeThat(accounts.size(), Is.is(1));
        assumeThat(accounts.get(0), Is.is(account));

        UserAccount actualAccount = accountManager.getAccount(UUID.randomUUID());
        assertEquals(null, actualAccount);
    }

    @Test
    public void deleteExistingAccount() throws InterruptedException, ExecutionException, TimeoutException {

        assumeThat(accountManager.getAllAccounts().size(), Is.is(0));

        UUID id = UUID.randomUUID();
        BigDecimal balance = BigDecimal.TEN;
        UserAccount account = accountManager.createNewAccount(id, balance);

        List<UserAccount> accounts = accountManager.getAllAccounts();
        assumeThat(accounts.size(), Is.is(1));
        assumeThat(accounts.get(0), Is.is(account));

        assertTrue(accountManager.deleteAccount(id));
        assertEquals(0, accountManager.getAllAccounts().size());
    }

    @Test
    public void deleteNotExistingAccount() throws InterruptedException, ExecutionException, TimeoutException {

        assumeThat(accountManager.getAllAccounts().size(), Is.is(0));

        UUID id = UUID.randomUUID();
        BigDecimal balance = BigDecimal.TEN;
        UserAccount account = accountManager.createNewAccount(id, balance);

        List<UserAccount> accounts = accountManager.getAllAccounts();
        assumeThat(accounts.size(), Is.is(1));
        assumeThat(accounts.get(0), Is.is(account));

        assertFalse(accountManager.deleteAccount(UUID.randomUUID()));
        assertEquals(1, accountManager.getAllAccounts().size());
    }
}
