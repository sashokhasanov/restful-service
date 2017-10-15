package ru.khasanov.rest.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Class representing user account.
 *
 * @author Aleksandr Khasanov
 */
public class UserAccount {

    private UUID userId;

    private BigDecimal balance;

    /**
     * Creates new instance of {@link UserAccount}.
     */
    public UserAccount() {
        userId = UUID.randomUUID();
        balance = BigDecimal.ZERO;
    }

    /**
     * Creates new instance of {@link UserAccount}.
     *
     * @param userId user id
     * @param balance initial balance
     */
    public UserAccount(UUID userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    /**
     * Get user id.
     *
     * @return user id
     */
    public UUID getId() {
        return userId;
    }

    /**
     * Get user balance.
     *
     * @return user balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Withdraws passed amount from user balance.
     *
     * @param amount amount to be withdrawn, must not be {@code null}
     */
    public void withdraw(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Attempting to withdraw negative amount: " + amount);
        }

        balance.subtract(amount);
    }

    /**
     * Adds passed amount to user balance.
     *
     * @param amount amount, must not be {@code null}
     */
    public void acquire(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Attempting to acquire negative amount: " + amount);
        }

        balance.add(amount);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof UserAccount)) {
            return false;
        }

        return userId.equals(((UserAccount) obj).getId());
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}