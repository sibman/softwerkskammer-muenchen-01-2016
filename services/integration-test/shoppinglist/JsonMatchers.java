package shoppinglist;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;

import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Provides several {@link Matcher} factories for use in tests which interpret
 * JSON data read from the application's services.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class JsonMatchers {

    private static final JsonParser jsonParser = new JsonParser();

    private JsonMatchers() {}

    /**
     * Returns a {@link Matcher} matching a {@link Map} which all of the given
     * matchers match.
     * 
     * <p>
     * This is synonymous with {@link CoreMatchers#allOf} and is included to
     * improve the readability of assertions.
     */
    @SafeVarargs
    public static Matcher<Map<String, ?>> anItemWith(
            Matcher<? super Map<String, ?>>... matchers) {
        return allOf(matchers);
    }

    /**
     * Returns a {@link Matcher} matching a {@link Map} containing an entry
     * mapping {@code field} to {@code value}.
     * 
     * <p>
     * This is synonymous with {@link Matchers#hasEntry} and is included to
     * improve the readability of assertions.
     */
    public static Matcher<Map<? extends String, ?>> anItemWithAnEntry(String field, Object value) {
        return hasEntry(field, value);
    }

    /**
     * Returns a {@link Matcher} matching a {@link Map} with the given key-value
     * entry.
     * 
     * <p>
     * This is synonymous with {@link Matchers#hasEntry} and is included to
     * improve the readability of assertions.
     */
    public static Matcher<Map<? extends String, ? extends Object>> entry(String key, Object value) {
        return hasEntry(key, value);
    }

    /**
     * Returns a {@link Matcher} matching a {@link String} representing JSON
     * which all of the given matchers match.
     * 
     * <p>
     * This is synonymous with {@link CoreMatchers#allOf} and is included to
     * improve the readability of assertions.
     */
    @SafeVarargs
    public static Matcher<String> havingJsonProperties(Matcher<? super String>... matchers) {
        return allOf(matchers);
    }

    /**
     * Returns a {@link PropertyMatcherFactory} for the given JSON property.
     */
    public static PropertyMatcherFactory<String, String> property(String name) {
        return PropertyMatcherFactory.property(name, s -> getPropertyAsString(s, name));
    }

    private static String getPropertyAsString(String jsonString, String name) {
        JsonElement property = jsonParser.parse(jsonString).getAsJsonObject().get(name);
        if (property != null) {
            return property.getAsString();
        } else {
            return null;
        }
    }
}
