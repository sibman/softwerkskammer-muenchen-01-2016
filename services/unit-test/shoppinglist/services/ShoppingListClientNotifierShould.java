package shoppinglist.services;

import static org.mockito.Mockito.verify;

import java.util.concurrent.BlockingQueue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import io.undertow.websockets.core.WebSocketChannel;
import shoppinglist.MockitoRule;

/**
 * Unit tests for {@link ShoppingListClientNotifier}.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ShoppingListClientNotifierShould {

    @Rule
    public MockitoRule mocks = MockitoRule.forTest(this);

    @Mock
    private BlockingQueue<ShoppingListClientNotifier.Message> queue;

    @Mock
    private WebSocketChannel channel;

    @Mock
    private Runnable action;

    private ShoppingListClientNotifier notifier;

    @Before
    public void setup() throws Exception {
        notifier = new ShoppingListClientNotifier(queue, channel);
    }

    @Test
    public void informObserversWhenSocketShutsDown() {
        notifier.addShutdownAction(action);

        notifier.run();

        verify(action).run();
    }
}
