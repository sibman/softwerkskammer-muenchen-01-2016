package shoppinglist.module;

import com.google.inject.AbstractModule;

/**
 * Top-level Guice module for the application.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class ShoppingListBackendModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DeploymentModule());
        install(new UndertowModule());
        install(new ApplicationModule());
    }
}
