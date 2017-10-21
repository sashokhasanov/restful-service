package ru.khasanov.rest;

import ru.khasanov.rest.manage.TransactionManager;
import ru.khasanov.rest.model.TransferTransaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Path("/transactions")
public class TransactionsResource {
    private TransactionManager transactionManager = ApplicationService.getInstance().getTransactionManager();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getAllTransactions() {
        return transactionManager.geAllTransactions();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getFromTransactions(@QueryParam("from") UUID fromId) {
        return transactionManager.getFromTransactions(fromId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransferTransaction> getToTransactions(@QueryParam("to") UUID fromId) {
        return transactionManager.getFromTransactions(fromId);
    }

    @POST
    @Path("/transfer")
    public Response transfer(@QueryParam("from") UUID fromId, @QueryParam("to") UUID toId, @QueryParam("amount") BigDecimal amount) {
        transactionManager.transfer(fromId, toId, amount);
        return Response.ok().build();
    }

}
