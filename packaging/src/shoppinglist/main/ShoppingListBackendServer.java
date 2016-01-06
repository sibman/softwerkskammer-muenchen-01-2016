package shoppinglist.main;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.undertow.Undertow;
import shoppinglist.module.ShoppingListBackendModule;

/**
 * The main entry point for the application. Launches an embedded web server
 * exposing RESTful endpoints to access the backend.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ShoppingListBackendServer {

    private final Injector injector = Guice.createInjector(new ShoppingListBackendModule());

    public static void main(String[] args) throws Exception {
        new ShoppingListBackendServer().run();
    }

    /**
     * Starts the server. Returns after the server has shut down.
     */
    public void run() {
        injector.getInstance(Undertow.class).start();
    }
}
