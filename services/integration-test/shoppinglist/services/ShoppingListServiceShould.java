package shoppinglist.services;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.put;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static shoppinglist.JsonMatchers.anItemWith;
import static shoppinglist.JsonMatchers.anItemWithAnEntry;
import static shoppinglist.JsonMatchers.entry;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import io.undertow.util.StatusCodes;
import shoppinglist.rules.ServerEndpointRule;

/**
 * Integration tests for the shopping list REST endpoints.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ShoppingListServiceShould {

    @Rule
    public final ServerEndpointRule serverRule = ServerEndpointRule.rule();

    @Test
    public void addAnItemToTheShoppingList() {
        put("/services/shopping-list/add?article=Butter&amount=100&unit=G");

        Response response = getShoppingList();

        assertThat(toShoppingList(response), contains(anItemWith(
                entry("article", "Butter"),
                entry("amount", BigDecimal.valueOf(100)),
                entry("unit", "G"),
                entry("stricken", false))));
    }

    @Test
    public void returnResponseOkWhenAddingItemSuccessfully() {
        put("/services/shopping-list/add?article=Butter&amount=100&unit=G")
                .then().assertThat().contentType(ContentType.JSON)
                .and().content("type", is("OK"));
    }

    @Test
    public void stikeAnItemOnTheShoppingList() {
        put("/services/shopping-list/add?article=Butter&amount=100&unit=G");
        put("/services/shopping-list/strike?article=Butter&unit=G")
                .then().assertThat().statusCode(StatusCodes.NO_CONTENT);

        Response response = getShoppingList();

        assertThat(toShoppingList(response), contains(anItemWithAnEntry("stricken", true)));
    }

    @Test
    public void returnEmptyListAfterClearingList() {
        put("/services/shopping-list/add?article=Butter&amount=100&unit=G");
        put("/services/shopping-list/clear")
                .then().assertThat().statusCode(StatusCodes.NO_CONTENT);

        Response response = getShoppingList();

        assertThat(toShoppingList(response), is(empty()));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toShoppingList(Response response) {
        return response.body().as(List.class);
    }

    private Response getShoppingList() {
        Response response = get("/services/shopping-list");
        response.then().assertThat().statusCode(StatusCodes.OK)
                .and().contentType(ContentType.JSON);
        return response;
    }
}
