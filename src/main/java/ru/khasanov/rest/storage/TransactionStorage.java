package ru.khasanov.rest.storage;

import ru.khasanov.rest.model.TransferTransaction;

import java.time.OffsetDateTime;
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
        } else {
            // TODO log
        }
    }

    /**
     * Get list of all transactions.
     *
     * @return {@link List} of all transactions.
     */
    public List<TransferTransaction> getAlTransactions()
    {
        return transactions;
    }

    /**
     * Get list of transactions for specific transmitter.
     *
     * @param from transmitter id. Must not be {@code null}
     * @return {@link List} of transactions for passed transmitter id
     */
    public List<TransferTransaction> getFromTransactions(UUID from) {
        Stream<TransferTransaction> transferTransactionStream =
                transactions.stream().filter(p -> from.equals(p.getFrom()));

        return transferTransactionStream.collect(Collectors.toList());
    }

    /**
     * Get list of transactions for specific recipient.
     *
     * @param to recipient id. Must not be {@code null}
     * @return {@link List} of transactions for passed recipient id
     */
    public List<TransferTransaction> getToTransactions(UUID to) {
        Stream<TransferTransaction> transferTransactionStream =
                transactions.stream().filter(p -> to.equals(p.getTo()));

        return transferTransactionStream.collect(Collectors.toList());
    }

    /**
     * Get list of transactions for specific period of time.
     *
     * @param fromDate start point. Must not be {@code null}
     * @param toDate   end point. Must not be {@code null}
     * @return {@link List} of transactions for specified period of time
     */
    public List<TransferTransaction> getDateTimeTransactions(OffsetDateTime fromDate, OffsetDateTime toDate) {
        Stream<TransferTransaction> transferTransactionStream =
                transactions.stream().filter(p -> fromDate.compareTo(p.getDateTime()) <= 0 && p.getDateTime().compareTo(toDate) <= 0);

        return transferTransactionStream.collect(Collectors.toList());
    }
}
