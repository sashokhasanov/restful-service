package ru.khasanov.rest;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Path("/transactions")
public class TransactionsResource {
    private TransactionManager transactionManager = ApplicationService.getInstance().getTransactionManager();

    /**
     * Get list of all transactions.
     *
     * @return list of all transactions
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getAllTransactions() {
        try {
            return transactionManager.geAllTransactions();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Request processing was interrupted");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request processing timed out");
        } catch (ExecutionException e) {
            throw new InternalServerErrorException("Internal error while request processing");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getFromTransactions(@QueryParam("from") UUID fromId) {
        try {
            return transactionManager.getFromTransactions(fromId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Request processing was interrupted");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request processing timed out");
        } catch (ExecutionException e) {
            throw new InternalServerErrorException("Internal error while request processing");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getToTransactions(@QueryParam("to") UUID fromId) {
        try {
            return transactionManager.getToTransactions(fromId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Request processing was interrupted");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request processing timed out");
        } catch (ExecutionException e) {
            throw new InternalServerErrorException("Internal error while request processing");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getDateTimeTransactions(@QueryParam("fromDate") OffsetDateTime fromDate, @QueryParam("toDate") OffsetDateTime toDate) {
        try {
            return transactionManager.getDateTimeTransactions(fromDate, toDate);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Request processing was interrupted");
        } catch (TimeoutException e) {
            throw new ServiceUnavailableException("Request processing timed out");
        } catch (ExecutionException e) {
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
        } catch (ExecutionException | IllegalArgumentException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
