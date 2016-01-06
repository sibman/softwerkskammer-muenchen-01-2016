package shoppinglist.services;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import shoppinglist.services.dto.ItemDto;

/**
 * Implements the exposed REST endpoints for the shopping list service.
 * 
 * <p>
 * This is the starting point for implementing the service.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
@ApplicationScoped
@Path("/shopping-list")
public final class ShoppingListService {

    @Inject
    ShoppingListService() {}

    /**
     * Returns the entire current shopping list.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDto> getWholeList() {
        return emptyList();
    }

    /**
     * Adds an item to the shopping list.
     * 
     * @param articleName
     *            the name of the article
     * @param amount
     *            the amount
     * @param unit
     *            a string representing the unit of the amount
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add")
    public void addItem(
            @QueryParam("name") String articleName,
            @QueryParam("amount") BigDecimal amount,
            @QueryParam("unit") String unitKey) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Marks the item identified by the given article and unit as completed.
     */
    @PUT
    @Path("/strike")
    public void strikeItem(
            @QueryParam("article") String articleName,
            @QueryParam("unit") String unitKey) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Clears the entire list of all items.
     */
    @PUT
    @Path("/clear")
    public void clearList() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Reverts the last action taken on the shopping list.
     */
    @PUT
    @Path("/undo")
    public void undoLastAction() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
