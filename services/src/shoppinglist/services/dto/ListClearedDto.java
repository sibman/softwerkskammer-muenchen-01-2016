package shoppinglist.services.dto;

/**
 * A DTO for the event to be sent when the shopping list is cleared of all
 * items.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ListClearedDto extends EventDto {

    /**
     * The value for {@code eventType} to be sent to the client.
     */
    public static final String EVENT_TYPE = "list-cleared";

    public ListClearedDto() {
        super(EVENT_TYPE);
    }
}
