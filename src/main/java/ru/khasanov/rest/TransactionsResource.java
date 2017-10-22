package ru.khasanov.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.khasanov.rest.manage.TransactionManager;
import ru.khasanov.rest.model.TransferTransaction;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Path("/transactions")
public class TransactionsResource {

    private TransactionManager transactionManager = ApplicationService.getInstance().getTransactionManager();

    private static Logger logger = LogManager.getLogger(TransactionsResource.class);

    /**
     * <p>Get list of transactions that match specific request query parameters.</p>
     * <p>Following parameters are accepted:</p>
     * <ul>
     * <li>from_id - specifies id of the transmitter. This parameter should match {@link UUID} string representation.</li>
     * <li>to_id - specifies id of the recipient. This parameter should match {@link UUID} string representation.</li>
     * <li>from_date - specifies beginning of time period. This parameter should match {@link OffsetDateTime} string representation.</li>
     * <li>to_date - specifies ending of time period. This parameter should match {@link OffsetDateTime} string representation.</li>
     * </ul>
     * <p>Parameters that are not supported are ignored while method execution.</p>
     *
     * @param info request uri information
     * @return {@link List} of transactions that match request query parameters
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getTransactions(@Context UriInfo info) {
        try {
            return transactionManager.getTransactions(info.getQueryParameters());
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

    @POST
    @Path("/transfer")
    public Response transfer(@QueryParam("from") UUID fromId, @QueryParam("to") UUID toId, @QueryParam("amount") BigDecimal amount) {
        try {
            transactionManager.transfer(fromId, toId, amount);
            return Response.ok().build();
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
