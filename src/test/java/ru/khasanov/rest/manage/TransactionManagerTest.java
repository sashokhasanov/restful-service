package ru.khasanov.rest.manage;

import org.junit.Before;
import org.junit.Test;
import ru.khasanov.rest.model.TransferTransaction;
import ru.khasanov.rest.model.UserAccount;
import ru.khasanov.rest.storage.AccountStorage;
import ru.khasanov.rest.storage.TransactionStorage;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests of {@link TransactionManager} class
 *
 * @author Aleksandr Khasanov
 */
public class TransactionManagerTest {

    private TransactionManager transactionManager;
    private AccountStorage accountStorage;

    @Before
    public void setUp() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        TransactionStorage transactionStorage = new TransactionStorage();
        accountStorage = new AccountStorage();
        transactionManager = new TransactionManager(transactionStorage, accountStorage, executorService);
    }

    @Test
    public void testTransferValidAmount() throws InterruptedException, ExecutionException, TimeoutException {

        UUID fromId = UUID.randomUUID();
        BigDecimal fromBalance = BigDecimal.valueOf(100);
        UserAccount fromAccount = new UserAccount(fromId, fromBalance);
        accountStorage.addAccount(fromAccount);

        UUID toId = UUID.randomUUID();
        BigDecimal toBalance = BigDecimal.valueOf(100);
        UserAccount toAccount = new UserAccount(toId, toBalance);
        accountStorage.addAccount(toAccount);

        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(50), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(150), toAccount.getBalance());
    }

    @Test(expected = ExecutionException.class)
    public void testTransferNegativeAmount() throws InterruptedException, ExecutionException, TimeoutException {
        UUID fromId = UUID.randomUUID();
        BigDecimal fromBalance = BigDecimal.valueOf(100);
        UserAccount fromAccount = new UserAccount(fromId, fromBalance);
        accountStorage.addAccount(fromAccount);

        UUID toId = UUID.randomUUID();
        BigDecimal toBalance = BigDecimal.valueOf(100);
        UserAccount toAccount = new UserAccount(toId, toBalance);
        accountStorage.addAccount(toAccount);

        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(50).negate());

        fail();
    }

    @Test(expected = ExecutionException.class)
    public void testTransferFromNotExistingAccount() throws InterruptedException, ExecutionException, TimeoutException {

        UUID fromId = UUID.randomUUID();

        UUID toId = UUID.randomUUID();
        BigDecimal toBalance = BigDecimal.valueOf(100);
        UserAccount toAccount = new UserAccount(toId, toBalance);
        accountStorage.addAccount(toAccount);

        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(50));

        fail();
    }

    @Test(expected = ExecutionException.class)
    public void testTransferToNotExistingAccount() throws InterruptedException, ExecutionException, TimeoutException {
        UUID fromId = UUID.randomUUID();
        BigDecimal fromBalance = BigDecimal.valueOf(100);
        UserAccount fromAccount = new UserAccount(fromId, fromBalance);
        accountStorage.addAccount(fromAccount);

        UUID toId = UUID.randomUUID();

        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(50));

        fail();
    }

    @Test(expected = ExecutionException.class)
    public void testTransferTooLargeAmount() throws InterruptedException, ExecutionException, TimeoutException {
        UUID fromId = UUID.randomUUID();
        BigDecimal fromBalance = BigDecimal.valueOf(100);
        UserAccount fromAccount = new UserAccount(fromId, fromBalance);
        accountStorage.addAccount(fromAccount);

        UUID toId = UUID.randomUUID();
        BigDecimal toBalance = BigDecimal.valueOf(100);
        UserAccount toAccount = new UserAccount(toId, toBalance);
        accountStorage.addAccount(toAccount);

        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(150));

        fail();
    }

    @Test
    public void testTransactionsCount() throws InterruptedException, ExecutionException, TimeoutException {

        UUID fromId = UUID.randomUUID();
        BigDecimal fromBalance = BigDecimal.valueOf(100);
        UserAccount fromAccount = new UserAccount(fromId, fromBalance);
        accountStorage.addAccount(fromAccount);

        UUID toId = UUID.randomUUID();
        BigDecimal toBalance = BigDecimal.valueOf(100);
        UserAccount toAccount = new UserAccount(toId, toBalance);
        accountStorage.addAccount(toAccount);

        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(10));
        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(10));
        transactionManager.transfer(fromId, toId, BigDecimal.valueOf(10));

        MultivaluedMap<String, String> parameters = new MultivaluedHashMap<>();
        List<TransferTransaction> transactions = transactionManager.getTransactions(parameters);

        assertEquals(3, transactions.size());
    }

    @Test
    public void testTransactionsFromAccount() throws InterruptedException, ExecutionException, TimeoutException {

        UUID id1 = UUID.randomUUID();
        BigDecimal balance1 = BigDecimal.valueOf(100);
        UserAccount account1 = new UserAccount(id1, balance1);
        accountStorage.addAccount(account1);

        UUID id2 = UUID.randomUUID();
        BigDecimal balance2 = BigDecimal.valueOf(100);
        UserAccount account2 = new UserAccount(id2, balance2);
        accountStorage.addAccount(account2);

        UUID id3 = UUID.randomUUID();
        BigDecimal balance3 = BigDecimal.valueOf(100);
        UserAccount account3 = new UserAccount(id3, balance3);
        accountStorage.addAccount(account3);

        transactionManager.transfer(id1, id2, BigDecimal.valueOf(10));
        transactionManager.transfer(id1, id3, BigDecimal.valueOf(10));
        transactionManager.transfer(id2, id3, BigDecimal.valueOf(10));

        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("from_id", id1.toString());
        List<TransferTransaction> transactions = transactionManager.getTransactions(queryParameters);

        assertEquals(2, transactions.size());
    }

    @Test
    public void testTransactionsToAccount() throws InterruptedException, ExecutionException, TimeoutException {

        UUID id1 = UUID.randomUUID();
        BigDecimal balance1 = BigDecimal.valueOf(100);
        UserAccount account1 = new UserAccount(id1, balance1);
        accountStorage.addAccount(account1);

        UUID id2 = UUID.randomUUID();
        BigDecimal balance2 = BigDecimal.valueOf(100);
        UserAccount account2 = new UserAccount(id2, balance2);
        accountStorage.addAccount(account2);

        UUID id3 = UUID.randomUUID();
        BigDecimal balance3 = BigDecimal.valueOf(100);
        UserAccount account3 = new UserAccount(id3, balance3);
        accountStorage.addAccount(account3);

        transactionManager.transfer(id1, id2, BigDecimal.valueOf(10));
        transactionManager.transfer(id1, id3, BigDecimal.valueOf(10));
        transactionManager.transfer(id2, id3, BigDecimal.valueOf(10));

        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("to_id", id3.toString());
        List<TransferTransaction> transactions = transactionManager.getTransactions(queryParameters);

        assertEquals(2, transactions.size());
    }

    @Test
    public void testTransactionsFromToAccount() throws InterruptedException, ExecutionException, TimeoutException {

        UUID id1 = UUID.randomUUID();
        BigDecimal balance1 = BigDecimal.valueOf(100);
        UserAccount account1 = new UserAccount(id1, balance1);
        accountStorage.addAccount(account1);

        UUID id2 = UUID.randomUUID();
        BigDecimal balance2 = BigDecimal.valueOf(100);
        UserAccount account2 = new UserAccount(id2, balance2);
        accountStorage.addAccount(account2);

        UUID id3 = UUID.randomUUID();
        BigDecimal balance3 = BigDecimal.valueOf(100);
        UserAccount account3 = new UserAccount(id3, balance3);
        accountStorage.addAccount(account3);

        transactionManager.transfer(id1, id2, BigDecimal.valueOf(10));
        transactionManager.transfer(id1, id3, BigDecimal.valueOf(10));
        transactionManager.transfer(id2, id3, BigDecimal.valueOf(10));

        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.add("from_id", id1.toString());
        queryParameters.add("to_id", id3.toString());
        List<TransferTransaction> transactions = transactionManager.getTransactions(queryParameters);

        assertEquals(1, transactions.size());
    }
}
