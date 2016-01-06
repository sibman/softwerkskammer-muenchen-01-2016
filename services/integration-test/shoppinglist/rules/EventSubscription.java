package shoppinglist.rules;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A {@link TestRule} which facilitates testing the Websockets-based
 * subscription feed of events on the shopping list.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class EventSubscription implements TestRule {

    private final WebSocketClient client = new WebSocketClient();
    private final ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
    private final URI subscriptionUri;

    private CountDownLatch messageLatch;
    private final List<String> messages = new ArrayList<>();

    /**
     * Creates a {@link EventScription} configured to access the given URI.
     */
    public static EventSubscription forUri(String uri) {
        try {
            return new EventSubscription(new URI(uri));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    private EventSubscription(URI subscriptionUri) {
        this.subscriptionUri = subscriptionUri;
    }

    /**
     * Awaits a message from the Websocket and asserts that the given
     * {@link Matcher} matches the received message.
     * 
     * If the server does not send a message, this method will not return. Thus
     * the use of a {@link Timeout} rule in any test using this method is
     * recommended.
     */
    public void assertThatMessageWasReceived(Matcher<? super String> message) throws Exception {
        if (messages.isEmpty()) {
            messageLatch = new CountDownLatch(1);
            messageLatch.await();
        }
        assertThat(messages, hasItem(message));
    }

    /**
     * Awaits a set of messages from the Websocket and asserts that the given
     * {@link Matcher} instances matche the received messages, irrespective of
     * order.
     * 
     * If the server does not send a message, this method will not return. Thus
     * the use of a {@link Timeout} rule in any test using this method is
     * recommended.
     */
    @SafeVarargs
    public final void assertThatMessagesWereReceived(Matcher<? super String>... messages)
            throws Exception {
        if (this.messages.size() < messages.length) {
            messageLatch = new CountDownLatch(messages.length - this.messages.size());
            messageLatch.await();
        }
        assertThat(this.messages, hasItems(messages));
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    String basicAuthHeader = Base64.getEncoder()
                            .encodeToString("admin:admin".getBytes());
                    upgradeRequest.setHeader("Authorization", "Basic " + basicAuthHeader);
                    client.start();
                    Session session = client
                            .connect(new EventSocket(), subscriptionUri, upgradeRequest).get();
                    assertThat(session.getUpgradeResponse().getStatusCode(), is(101));
                    // TODO: This seems to be necessary to avoid flaky tests
                    // which use this rule. Figure out why and eliminate the
                    // cause.
                    Thread.sleep(20);
                    base.evaluate();
                } finally {
                    client.stop();
                }
            }
        };
    }

    @WebSocket(maxTextMessageSize = 8192)
    private class EventSocket {
        @OnWebSocketMessage
        public void onMessage(String message) {
            if (messageLatch != null) {
                messageLatch.countDown();
            }
            messages.add(message);
        }
    }
}
