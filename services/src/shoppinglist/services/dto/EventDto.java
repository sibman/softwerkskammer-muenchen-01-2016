package shoppinglist.services.dto;

/**
 * The base class for all DTOs representing events on the shopping list.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public abstract class EventDto {
    /**
     * A string representing the type of the event.
     */
    public final String eventType;

    protected EventDto(String eventType) {
        this.eventType = eventType;
    }
}
