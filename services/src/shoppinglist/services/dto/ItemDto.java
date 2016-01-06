package shoppinglist.services.dto;

import java.math.BigDecimal;

/**
 * A DTO representing a shopping list item. To be converted to JSON and sent to
 * the client.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ItemDto {
    public final String article;
    public final BigDecimal amount;
    public final String unit;
    public final boolean stricken;

    public ItemDto(String article, BigDecimal amount, String unit, boolean stricken) {
        this.article = article;
        this.amount = amount;
        this.unit = unit;
        this.stricken = stricken;
    }
}
