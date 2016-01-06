package shoppinglist.services;

import static org.mockito.Mockito.verify;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import shoppinglist.MockitoRule;

/**
 * Unit tests for {@link ShoppingListSubscriptionService}.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ShoppingListSubscriptionServiceTest {

    @Rule
    public final MockitoRule mocks = MockitoRule.forTest(this);

    @Mock
    private BlockingQueue<ShoppingListClientNotifier.Message> queue;

    @Mock
    private ExecutorService executor;

    @Mock
    private WebSocketHttpExchange exchange;

    @Mock
    private WebSocketChannel channel;

    private ShoppingListClientNotifier notifier;
    private ShoppingListSubscriptionService service;

    @Before
    public void setupQueue() throws Exception {
        notifier = new ShoppingListClientNotifier(queue, channel);
        service = new ShoppingListSubscriptionService(
                executor, eventOutput -> notifier);
    }

    @Test
    public void eventOutputShouldBeClosedWhenConnectionIsLost() throws Exception {
        service.onConnect(exchange, channel);

        notifier.run();

        verify(channel).close();
    }
}
