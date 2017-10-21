package ru.khasanov.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import ru.khasanov.rest.manage.AccountManager;
import ru.khasanov.rest.manage.TransactionManager;
import ru.khasanov.rest.storage.AccountStorage;
import ru.khasanov.rest.storage.TransactionStorage;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main application class.
 *
 * @author Aleksandr Khasanov
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/rest/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in ru.khasanov.rest package
        final ResourceConfig rc = new ResourceConfig().packages("ru.khasanov.rest");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     *
     * @param args array of {@link String} arguments.
     */
    public static void main(String[] args) {

        initApplicationService();

        final HttpServer server = startServer();

        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.shutdownNow();
        }
    }

    private static void initApplicationService() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        AccountStorage accountStorage = new AccountStorage();
        AccountManager accountManager = new AccountManager(accountStorage, executorService);
        accountManager.setTimeout(1000);
        ApplicationService.getInstance().initAccountManager(accountManager);

        TransactionStorage transactionStorage = new TransactionStorage();
        TransactionManager transactionManager = new TransactionManager(transactionStorage, accountStorage, executorService);
        transactionManager.setTimeout(1000);
        ApplicationService.getInstance().initTransactionManager(transactionManager);
    }
}

