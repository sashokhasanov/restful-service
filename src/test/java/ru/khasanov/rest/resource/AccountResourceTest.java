package ru.khasanov.rest.resource;

import org.glassfish.grizzly.http.server.HttpServer;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.khasanov.rest.Main;
import ru.khasanov.rest.model.UserAccount;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

/**
 * Tests of {@link AccountResource} class
 *
 * @author Aleksandr Khasanov
 */
public class AccountResourceTest {

    private HttpServer server;

    private WebTarget target;

    @Before
    public void setUp() {

        Main.initApplicationService();
        server = Main.startServer();

        Client client = ClientBuilder.newClient();
        target = client.target(Main.BASE_URI).path(AccountResource.ACCOUNTS);
    }

    @After
    public void tearDown() {
        server.shutdownNow();
    }

    @Test
    public void testCreateAccount() {
        Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testRequestAccountsList() {
        List<UserAccount> accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assertTrue(accounts.isEmpty());
    }

    @Test
    public void testCreateAccountRequestAccountList() {
        List<UserAccount> accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(0));

        Response responseMsg = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assertEquals(1, accounts.size());
    }

    @Test
    public void testCreate2AccountsRequestAccountList() {
        List<UserAccount> accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(0));

        Response responseMsg = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(1));

        responseMsg = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assertEquals(2, accounts.size());
    }

    @Test
    public void testGetExistingAccount() {
        List<UserAccount> accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(0));

        Response responseMsg = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        Response responseMsg2 = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg2.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(2));

        String id = accounts.get(0).getUserId().toString();

        UserAccount account = target.path(id).request(MediaType.APPLICATION_JSON).get(UserAccount.class);

        assertEquals(id, account.getUserId().toString());
    }

    @Test
    public void testGetNotExistingAccount() {
        List<UserAccount> accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(0));

        Response responseMsg = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        Response responseMsg2 = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg2.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(2));

        String id = UUID.randomUUID().toString();

        Response response = target.path(id).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteExistingAccount() {

        List<UserAccount> accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(0));

        Response responseMsg = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        Response responseMsg2 = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg2.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(2));

        String id = accounts.get(0).getUserId().toString();
        Response deleteResponse = target.path(id).request(MediaType.APPLICATION_JSON).delete();

        assertEquals(Response.Status.OK.getStatusCode(), deleteResponse.getStatus());

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });

        assertEquals(1, accounts.size());
        assertFalse(accounts.get(0).getUserId().toString().equals(id));
    }

    @Test
    public void testDeleteNotExistingAccount() {

        List<UserAccount> accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(0));

        Response responseMsg = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        Response responseMsg2 = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
        assumeThat(responseMsg2.getStatus(), Is.is(Response.Status.CREATED.getStatusCode()));

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });
        assumeThat(accounts.size(), Is.is(2));

        String id = UUID.randomUUID().toString();
        Response deleteResponse = target.path(id).request(MediaType.APPLICATION_JSON).delete();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), deleteResponse.getStatus());

        accounts = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<UserAccount>>() {
        });

        assertEquals(2, accounts.size());
    }

}
