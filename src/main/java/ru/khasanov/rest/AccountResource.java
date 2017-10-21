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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
     * Get all user accounts.
     *
     * @return Collection of all user accounts
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserAccount> getAllAccounts() {

        try {
            return accountManager.getAllAccounts();
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException("Request failed");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request timed out");
        }
    }

    /**
     * Get user account by id.
     *
     * @param userId user id. Must not be {@code null}
     * @return {@link UserAccount} for specified id
     */
    @GET
    @Path(USER)
    @Produces(MediaType.APPLICATION_JSON)
    public UserAccount getAccount(@PathParam(USER_ID) UUID userId) {

        try {
            UserAccount account = accountManager.getAccount(userId);
            if (account != null) {
                return account;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException("Request failed");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request timed out");
        }

        throw new NotFoundException("Account not found: " + userId);
    }

    /**
     * Create new user account.
     *
     * @return {@link Response} specifying result of operation
     */
    @POST
    public Response createAccount() {

        try {
            UserAccount account = accountManager.createNewAccount();
            return Response.created(URI.create(ACCOUNTS + "/" + account.getUserId())).build();

        } catch (InterruptedException | ExecutionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (TimeoutException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        }
    }

    /**
     * Create new user account.
     *
     * @param userId  user id. Must not be {@code null}
     * @param balance initial balance. Must not be {@code null}
     * @return {@link Response} specifying result of operation
     */
    @POST
    public Response createAccount(@QueryParam("id") UUID userId, @QueryParam("balance") BigDecimal balance) {
        try {
            UserAccount account = accountManager.createNewAccount(userId, balance);
            return Response.created(URI.create(ACCOUNTS + "/" + account.getUserId())).build();
        } catch (InterruptedException | ExecutionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (TimeoutException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        }
    }

    /**
     * Delete user account
     *
     * @param userId user id. Must not be {@code null}
     * @return {@link Response} specifying result of operation
     */
    @DELETE
    @Path(USER)
    public Response deleteAccount(@PathParam(USER_ID) UUID userId) {

        try {
            if (accountManager.deleteAccount(userId)) {
                return Response.ok().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (InterruptedException | ExecutionException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (TimeoutException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        }
    }
}
