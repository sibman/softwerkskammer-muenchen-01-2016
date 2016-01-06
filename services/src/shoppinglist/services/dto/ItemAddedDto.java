package shoppinglist.services.dto;

/**
 * A DTO to be sent when an item is added to the shopping list.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ItemAddedDto extends EventDto {

    /**
     * The value for {@code eventType} to be sent to the client.
     */
    public static final String EVENT_TYPE = "item-added";

    /**
     * A DTO containing the new item.
     */
    public final ItemDto itemDto;

    public ItemAddedDto(ItemDto itemDto) {
        super(EVENT_TYPE);
        this.itemDto = itemDto;
    }
}
