package ru.khasanov.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Class representing transaction for money transfer between user accounts.
 *
 * @author Aleksandr Khasanov
 */
@XmlRootElement
public class TransferTransaction {

    @XmlElement
    private UUID from;

    @XmlElement
    private UUID to;

    @XmlElement
    private BigDecimal amount;

    @XmlElement
    private long timestamp;

    /**
     * Creates new instance of {@link TransferTransaction}
     * This required for JAXB and should not be used to to create instances of {@link TransferTransaction}.
     */
    public TransferTransaction() {
    }

    /**
     * Creates new instance of {@link TransferTransaction}.
     *
     * @param from      transmitter id
     * @param to        recipient id
     * @param amount    transfer amount
     * @param timestamp timestamp of transfer operation
     */
    public TransferTransaction(UUID from, UUID to, BigDecimal amount, long timestamp) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = timestamp;
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
     * Get transfer timestamp.
     *
     * @return transfer timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferTransaction that = (TransferTransaction) o;

        if (timestamp != that.timestamp) return false;
        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;
        return amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}