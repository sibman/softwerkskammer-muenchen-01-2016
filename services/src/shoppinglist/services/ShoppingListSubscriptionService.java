package shoppinglist.services;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

/**
 * Implements the subscription service which notifies clients via a Websocket of
 * changes to the shopping list.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class ShoppingListSubscriptionService implements WebSocketConnectionCallback {
    private static final Logger log = LoggerFactory
            .getLogger(ShoppingListSubscriptionService.class);

    private final ExecutorService executor;
    private final ShoppingListClientNotifier.Factory clientNotifierFactory;

    @Inject
    ShoppingListSubscriptionService(
            ExecutorService executor,
            ShoppingListClientNotifier.Factory clientNotifierFactory) {
        this.executor = executor;
        this.clientNotifierFactory = clientNotifierFactory;
    }

    /**
     * Called when a client connects. Constructs a
     * {@link ShoppingListClientNotifier} and sets it up to listen to events.
     * 
     * <p>
     * Here it will be necessary to add some code.
     */
    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        ShoppingListClientNotifier clientNotifier = clientNotifierFactory.create(channel);
        clientNotifier.addShutdownAction(() -> closeChannel(channel));

        executor.submit(clientNotifier);
    }

    private void closeChannel(WebSocketChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            log.error("Error closing WebSocketChannel", e);
        }
    }
}
