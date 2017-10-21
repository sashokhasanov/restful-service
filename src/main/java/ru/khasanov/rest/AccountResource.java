package ru.khasanov.rest;

import ru.khasanov.rest.manage.AccountManager;
import ru.khasanov.rest.model.UserAccount;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;

/**
 * Root resource for managing user accounts.
 *
 * @author Aleksandr Khasanov
 */
@Path(AccountResource.ACCOUNTS)
public class AccountResource {

    public static final String ACCOUNTS = "/accounts";

    private static final String USER_ID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private static final String USER_ID = "id";

    private static final String USER = "/{" + USER_ID + ": " + USER_ID_PATTERN + "}";

    private AccountManager accountManager = ApplicationService.getInstance().getAccountManager();

    /**
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserAccount> getUsers() {

        return accountManager.getAllAccounts();
    }

    @GET
    @Path(USER)
    @Produces(MediaType.APPLICATION_JSON)
    public UserAccount getUser(@PathParam(USER_ID) UUID userId) throws NotFoundException {

        UserAccount account = accountManager.getAccount(userId);
        if (account == null) {
            throw new NotFoundException("Account not found: " + userId);
        }

        return account;
    }

    @POST
    public Response createUserAccount() {
        UserAccount account = accountManager.createNewAccount();
        return Response.created(URI.create(ACCOUNTS + "/" + account.getUserId())).build();
    }

    @POST
    public Response createUserAccount(@QueryParam("id") UUID userId, @QueryParam("balance") BigDecimal balance) {
        UserAccount account = accountManager.createNewAccount(userId, balance);
        return Response.created(URI.create(ACCOUNTS + "/" + account.getUserId())).build();
    }

    @DELETE
    @Path(USER)
    public Response deleteUserAccount(@PathParam(USER_ID) UUID userId) {

        if (accountManager.deleteAccount(userId)) {
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
