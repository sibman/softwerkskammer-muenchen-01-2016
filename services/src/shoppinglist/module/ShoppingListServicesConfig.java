package shoppinglist.module;

import javax.inject.Inject;

import org.glassfish.jersey.server.ResourceConfig;

import com.google.inject.Provider;

import shoppinglist.services.ShoppingListService;
import shoppinglist.services.ShoppingListSubscriptionService;

/**
 * Configures the Jersey servlet with objects which serve REST endpoints and
 * other resources.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
final class ShoppingListServicesConfig extends ResourceConfig {

    @Inject
    ShoppingListServicesConfig(
            Provider<ShoppingListService> shoppingListService,
            Provider<ShoppingListSubscriptionService> subscriptionService) {
        register(GsonWriter.class);
        register(shoppingListService.get());
        register(subscriptionService.get());
    }
}
