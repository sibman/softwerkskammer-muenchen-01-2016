package shoppinglist.services;

import static com.jayway.restassured.RestAssured.put;
import static shoppinglist.JsonMatchers.havingJsonProperties;
import static shoppinglist.JsonMatchers.property;
import static shoppinglist.JsonMatchers.subobjectWithProperty;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.Timeout;

import shoppinglist.rules.EventSubscription;
import shoppinglist.rules.ServerEndpointRule;
import shoppinglist.services.dto.ItemAddedDto;

/**
 * Integration tests for the shopping list subscription Websocket.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class ShoppingListSubscriptionShould {
    private static final String SUBSCRIPTION_URI =
            "ws://localhost:8080/services/shopping-list/subscribe";

    private final ServerEndpointRule serverRule = ServerEndpointRule.rule();
    private final EventSubscription eventSubscription = EventSubscription.forUri(SUBSCRIPTION_URI);
    private final Timeout globalTimeout = new Timeout(10, TimeUnit.SECONDS);

    @Rule
    public final RuleChain rules = RuleChain
    .outerRule(globalTimeout).around(serverRule).around(eventSubscription);

    @Test
    public void notifyOfChangesToShoppingList() throws Exception {
        put("/services/shopping-list/add?article=Butter&amount=100&unit=G");

        eventSubscription.assertThatMessageWasReceived(havingJsonProperties(
                property("eventType").equalTo(ItemAddedDto.EVENT_TYPE),
                subobjectWithProperty("itemDto", "article").equalTo("Butter"),
                subobjectWithProperty("itemDto", "amount").equalTo("100"),
                subobjectWithProperty("itemDto", "unit").equalTo("G")));
    }
}
