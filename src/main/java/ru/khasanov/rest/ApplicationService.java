package ru.khasanov.rest;

import ru.khasanov.rest.manage.AccountManager;
import ru.khasanov.rest.manage.TransactionManager;

/**
 * Application helper service.
 *
 * @author Aleksandr Khasanov
 */
public class ApplicationService {

    private static final ApplicationService INSTANCE = new ApplicationService();

    private AccountManager accountManager;

    private TransactionManager transactionManager;

    private ApplicationService() {
    }

    /**
     * Get instance of {@link ApplicationService}.
     *
     * @return instance of {@link ApplicationService}
     */
    public static ApplicationService getInstance() {
        return INSTANCE;
    }

    /**
     * Init account manager.
     *
     * @param accountManager {@link AccountManager}. Must not be {@code null}
     */
    public void initAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Init transaction manager.
     *
     * @param transactionManager {@link TransactionManager}. Must not be {@code null}
     */
    public void initTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Get account manager.
     *
     * @return instance of {@link AccountManager}
     */
    public AccountManager getAccountManager() {
        return accountManager;
    }

    /**
     * Get transaction manager.
     *
     * @return instance of {@link TransactionManager}
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}
