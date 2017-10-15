package ru.khasanov.rest.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Class representing transaction for money transfer between user accounts.
 *
 * @author Aleksandr Khasanov
 */
public class TransferTransaction {

    private UUID from;

    private UUID to;

    private BigDecimal amount;

    private OffsetDateTime dateTime;

    /**
     * Creates new instance of {@link TransferTransaction}.
     *
     * @param from     transmitter id
     * @param to       recipient id
     * @param amount   transfer amount
     * @param dateTime date and time of transfer operation
     */
    public TransferTransaction(UUID from, UUID to, BigDecimal amount, OffsetDateTime dateTime) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    /**
     * Get transmitter id.
     *
     * @return transmitter id
     */
    public UUID getFrom() {
        return from;
    }

    /**
     * Get recipient id.
     *
     * @return recipient id.
     */
    public UUID getTo() {
        return to;
    }

    /**
     * Get transfer amount.
     *
     * @return transfer amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Get transfer date and time.
     *
     * @return transfer date and time
     */
    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferTransaction that = (TransferTransaction) o;

        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;
        if (!amount.equals(that.amount)) return false;
        return dateTime.equals(that.dateTime);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + dateTime.hashCode();
        return result;
    }

}