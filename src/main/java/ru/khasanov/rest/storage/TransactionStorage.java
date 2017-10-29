package ru.khasanov.rest.storage;

import ru.khasanov.rest.model.TransferTransaction;
import ru.khasanov.rest.resource.TransactionsRequestParameters;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In-memory storage for transfer transactions.
 *
 * @author Aleksandr Khasanov
 */
public class TransactionStorage {

    private List<TransferTransaction> transactions = new ArrayList<>();

    /**
     * Add transaction to storage.
     *
     * @param transaction transaction to be added. Must not be {@code null}
     */
    public void addTransaction(TransferTransaction transaction) {
        UUID from = transaction.getFrom();
        UUID to = transaction.getTo();

        if (!from.equals(to)) {
            transactions.add(transaction);
        }
    }

    /**
     * <p>Get list of transactions that match specific query parameters.</p>
     * <p>Following parameters are accepted:</p>
     * <ul>
     * <li>from_id - specifies id of the transmitter. This parameter should match {@link UUID} string representation.</li>
     * <li>to_id - specifies id of the recipient. This parameter should match {@link UUID} string representation.</li>
     * <li>from_date - specifies beginning of time period. </li>
     * <li>to_date - specifies ending of time period. </li>
     * </ul>
     * <p>Parameters that are not supported are ignored while method execution.</p>
     *
     * @param queryParameters map of query parameters. Must not be {@code null}
     * @return {@link List} of transactions that match passed query parameters.
     */
    public List<TransferTransaction> getTransactions(MultivaluedMap<String, String> queryParameters) {
        Stream<TransferTransaction> transferTransactionStream = transactions.stream();

        String fromIdString = queryParameters.getFirst(TransactionsRequestParameters.FROM_ID);
        if (fromIdString != null && !fromIdString.isEmpty()) {
            final UUID fromId = UUID.fromString(fromIdString);
            transferTransactionStream = transferTransactionStream.filter(p -> fromId.equals(p.getFrom()));
        }

        String toIdString = queryParameters.getFirst(TransactionsRequestParameters.TO_ID);
        if (toIdString != null && !toIdString.isEmpty()) {
            final UUID toId = UUID.fromString(toIdString);
            transferTransactionStream = transferTransactionStream.filter(p -> toId.equals(p.getTo()));
        }

        String fromTimestampString = queryParameters.getFirst(TransactionsRequestParameters.FROM_DATE);
        if (fromTimestampString != null && !fromTimestampString.isEmpty()) {
            final long fromTimestamp = Long.valueOf(fromTimestampString);
            transferTransactionStream = transferTransactionStream.filter(p -> fromTimestamp <= p.getTimestamp());
        }

        String toTimestampString = queryParameters.getFirst(TransactionsRequestParameters.TO_DATE);
        if (toTimestampString != null && !toTimestampString.isEmpty()) {

            final long toTimestamp = Long.valueOf(toTimestampString);
            transferTransactionStream = transferTransactionStream.filter(p -> p.getTimestamp() <= toTimestamp);
        }

        return transferTransactionStream.collect(Collectors.toList());
    }
}
