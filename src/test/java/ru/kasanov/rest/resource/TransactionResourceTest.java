package ru.kasanov.rest.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.khasanov.rest.Main;
import ru.khasanov.rest.model.TransferTransaction;
import ru.khasanov.rest.model.UserAccount;
import ru.khasanov.rest.resource.AccountResource;
import ru.khasanov.rest.resource.TransactionsResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

/**
 * Tests of {@link TransactionsResource} class
 *
 * @author Aleksandr Khasanov
 */
public class TransactionResourceTest {

    private HttpServer server;

    private WebTarget target;

    @Before
    public void setUp() {
        Main.initApplicationService();
        server = Main.startServer();

        Client client = ClientBuilder.newClient();
        target = client.target(Main.BASE_URI);
    }

    @After
    public void tearDown() {
        server.shutdownNow();
    }

    @Test
    public void testTransferValidAmount() {

        UUID from = createUserAccount();
        UUID to = createUserAccount();

        Response transferResponse = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assertEquals(Response.Status.OK.getStatusCode(), transferResponse.getStatus());


        UserAccount fromAccount = getAccount(from);
        assertEquals(BigDecimal.valueOf(90), fromAccount.getBalance());

        UserAccount toAccount = getAccount(to);
        assertEquals(BigDecimal.valueOf(110), toAccount.getBalance());
    }

    @Test
    public void testTransferNegativeAmount() {
        UUID from = createUserAccount();
        UUID to = createUserAccount();

        Response transferResponse = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("amount", new BigDecimal(10).negate())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), transferResponse.getStatus());
    }


    @Test
    public void testTransferFromNotExistingAccount() {
        UUID from = UUID.randomUUID();
        UUID to = createUserAccount();

        Response transferResponse = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("amount", new BigDecimal(10).negate())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), transferResponse.getStatus());
    }

    @Test
    public void testTransferToNotExistingAccount() {
        UUID from = createUserAccount();
        UUID to = UUID.randomUUID();

        Response transferResponse = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("amount", new BigDecimal(10).negate())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), transferResponse.getStatus());
    }

    @Test
    public void testGetAllTransactions() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        UUID id1 = createUserAccount();
        UUID id2 = createUserAccount();
        UUID id3 = createUserAccount();

        Response transferResponse1 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id2)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse1.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse2 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse2.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse3 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id2)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse3.getStatus(), Is.is(Response.Status.OK.getStatusCode()));


        List<TransferTransaction> transactions = target.
                path(TransactionsResource.TRANSACTIONS)
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<TransferTransaction>>() {
                });

        assertEquals(3, transactions.size());
    }

    @Test
    public void testGetFromTransactions() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        UUID id1 = createUserAccount();
        UUID id2 = createUserAccount();
        UUID id3 = createUserAccount();

        Response transferResponse1 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id2)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse1.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse2 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse2.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse3 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id2)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse3.getStatus(), Is.is(Response.Status.OK.getStatusCode()));


        List<TransferTransaction> transactions = target.
                path(TransactionsResource.TRANSACTIONS)
                .queryParam("from_id", id1)
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<TransferTransaction>>() {
                });

        assertEquals(2, transactions.size());
    }

    @Test
    public void testGetToTransactions() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        UUID id1 = createUserAccount();
        UUID id2 = createUserAccount();
        UUID id3 = createUserAccount();

        Response transferResponse1 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id2)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse1.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse2 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse2.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse3 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id2)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse3.getStatus(), Is.is(Response.Status.OK.getStatusCode()));


        List<TransferTransaction> transactions = target.
                path(TransactionsResource.TRANSACTIONS)
                .queryParam("to_id", id2)
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<TransferTransaction>>() {
                });

        assertEquals(1, transactions.size());
    }

    @Test
    public void testGetFromToTransactions() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        UUID id1 = createUserAccount();
        UUID id2 = createUserAccount();
        UUID id3 = createUserAccount();

        Response transferResponse1 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id2)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse1.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse2 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id1)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse2.getStatus(), Is.is(Response.Status.OK.getStatusCode()));

        Response transferResponse3 = target
                .path(TransactionsResource.TRANSACTIONS + TransactionsResource.TRANSFER)
                .queryParam("from", id2)
                .queryParam("to", id3)
                .queryParam("amount", new BigDecimal(10))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));

        assumeThat(transferResponse3.getStatus(), Is.is(Response.Status.OK.getStatusCode()));


        List<TransferTransaction> transactions = target.
                path(TransactionsResource.TRANSACTIONS)
                .queryParam("from_id", id1)
                .queryParam("to_id", id3)
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<TransferTransaction>>() {
                });

        assertEquals(1, transactions.size());
    }

    private UUID createUserAccount() {
        UUID id = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(100);

        Response responseMsg = target.path(AccountResource.ACCOUNTS + "/")
                .queryParam("id", id.toString())
                .queryParam("balance", balance)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        return id;
    }

    private UserAccount getAccount(UUID id) {
        return target.path(AccountResource.ACCOUNTS).path(id.toString()).request(MediaType.APPLICATION_JSON).get(UserAccount.class);
    }
}
