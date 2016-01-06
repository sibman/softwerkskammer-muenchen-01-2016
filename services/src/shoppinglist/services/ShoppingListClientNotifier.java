package shoppinglist.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import shoppinglist.services.dto.EventDto;

/**
 * Receives notifications of events on the shopping list and forwards them
 * through a Websocket to the client.
 * 
 * <p>
 * After starting, it waits for at most one minute for events. If no events
 * comes, then it times out and closes the Websocket. The clock is reset
 * whenever an event arrives.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class ShoppingListClientNotifier implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ShoppingListClientNotifier.class);

    private final WebSocketChannel channel;
    private final BlockingQueue<Message> queue;
    private final List<Runnable> shutdownActions = new ArrayList<>();

    /**
     * Abstract factory for {@link ShoppingListClientNotifier} instances.
     * 
     * @author Bradford Hovinen <hovinen@gmail.com>
     */
    public interface Factory {
        ShoppingListClientNotifier create(WebSocketChannel channel);
    }

    /**
     * Concrete factory for {@link ShoppingListClientNotifier} instances.
     * 
     * @author Bradford Hovinen <hovinen@gmail.com>
     */
    public static final class ConcreteFactory implements Factory {

        @Override
        public ShoppingListClientNotifier create(WebSocketChannel channel) {
            BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(10);
            return new ShoppingListClientNotifier(queue, channel);
        }
    }

    ShoppingListClientNotifier(BlockingQueue<Message> queue, WebSocketChannel channel) {
        this.queue = queue;
        this.channel = channel;
    }

    /**
     * Adds a {@link Runnable} to be executed when the Websocket shuts down.
     */
    public void addShutdownAction(Runnable action) {
        shutdownActions.add(action);
    }

    @Override
    public void run() {
        try {
            Message event;
            while ((event = queue.poll(1, TimeUnit.MINUTES)) != null) {
                writeEvent(event);
            }
        } catch (InterruptedException e) {
            log.info("run: Interrupted", e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("Error writing event", e);
        } finally {
            shutdown();
        }
    }

    private void writeEvent(Message event) throws IOException {
        String payload = event.toPayload();
        log.info("run: Sending event, payload = " + payload);
        WebSockets.sendText(payload, channel, null);
    }

    private void shutdown() {
        for (Runnable action : shutdownActions) {
            action.run();
        }
    }

    /**
     * Encapsulates a message to be sent to the client.
     * 
     * @author Bradford Hovinen <hovinen@gmail.com>
     */
    static class Message {
        private static final Gson gson = new Gson();

        private final EventDto dto;

        /**
         * Constructs a message out of the given {@link EventDto} instance.
         */
        public static Message fromDto(EventDto dto) {
            return new Message(dto);
        }

        private Message(EventDto dto) {
            this.dto = dto;
        }

        /**
         * Converts the message from a DTO into the actual payload as JSON to be
         * sent.
         */
        public String toPayload() {
            return gson.toJson(dto);
        }
    }
}
