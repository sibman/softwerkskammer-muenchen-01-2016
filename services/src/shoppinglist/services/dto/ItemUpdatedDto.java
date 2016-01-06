package shoppinglist.services.dto;

/**
 * A DTO to be sent when an shopping list item is updated.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ItemUpdatedDto extends EventDto {

    /**
     * The value for {@code eventType} to be sent to the client.
     */
    private static final String EVENT_TYPE = "item-updated";

    /**
     * A DTO containing the new values for the item.
     */
    public final ItemDto itemDto;

    public ItemUpdatedDto(ItemDto itemDto) {
        super(EVENT_TYPE);
        this.itemDto = itemDto;
    }
}
