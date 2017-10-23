package ru.khasanov.rest.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.khasanov.rest.ApplicationService;
import ru.khasanov.rest.manage.AccountManager;
import ru.khasanov.rest.model.UserAccount;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
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

    private static Logger logger = LogManager.getLogger(AccountResource.class);

    private AccountManager accountManager = ApplicationService.getInstance().getAccountManager();

    /**
     * Get all user accounts.
     *
     * @return {@link List} of all user accounts
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserAccount> getAllAccounts() {

        try {
            return accountManager.getAllAccounts();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Request processing was interrupted");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request processing timed out");
        } catch (ExecutionException e) {
            logger.warn("Internal server error" + e.getMessage());
            throw new InternalServerErrorException("Internal error while request processing");
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Request processing was interrupted");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request processing timed out");
        } catch (ExecutionException e) {
            logger.warn("Internal server error" + e.getMessage());
            throw new InternalServerErrorException("Internal error while request processing");
        }

        logger.warn("Account with following id not found: {}", userId);
        throw new NotFoundException("Account not found: " + userId);
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

            if (userId == null) {
                userId = UUID.randomUUID();
            }

            if (balance == null) {
                balance = BigDecimal.ZERO;
            }

            UserAccount account = accountManager.createNewAccount(userId, balance);
            return Response.created(URI.create(ACCOUNTS + "/" + account.getUserId())).build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Response.status(Response.Status.NOT_MODIFIED).build();
        } catch (TimeoutException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        } catch (ExecutionException e) {
            logger.warn("Internal server error" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Response.status(Response.Status.NOT_MODIFIED).build();
        } catch (TimeoutException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        } catch (ExecutionException e) {
            logger.warn("Internal server error" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
