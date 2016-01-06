package shoppinglist;

import java.util.function.Function;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * Creates {@link Matcher} instances to match a property in an objects.
 * 
 * <p>
 * This can be used to create matchers to be used with {@link Assert#assertThat}
 * as follows. Assume that {@code MyClass} has a property {@code name},
 * accessible via the getter {@code MyClass#getName} and that {@code myObject}
 * is an instance of {@code MyClass} on which the test is to assert that its
 * property {@code name} is equal to {@code "Java"}. Then the assertion is as
 * follows:
 * {@code assertThat(myObject, property("name", MyClass::getName).is("Java"));}
 * 
 * <p>
 * It is possible to employ other matchers on the property in question via the
 * method {@link #matches}.
 * 
 * <p>
 * This is an alternative to instantiating {@link FeatureMatcher} instances,
 * taking advantage of lambda expressions to reduce boilerplate.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 *
 * @param <T>
 *            The class to be matched by {@link Matcher} instances created by
 *            this instance.
 * @param <U>
 *            The class of the property being matched.
 */
public final class PropertyMatcherFactory<T, U> {

    private final Function<T, U> extractor;
    private final String propertyName;

    /**
     * Creates an instance to match the given property.
     * 
     * @param propertyName
     *            the name of the property to be matched. Used to create a
     *            sensible message upon test failure.
     * @param extractor
     *            a {@link Function} to extract the property from an instance of
     *            the class {@link <T>}.
     */
    public static <T, U> PropertyMatcherFactory<T, U> property(
            String propertyName, Function<T, U> extractor) {
        return new PropertyMatcherFactory<>(propertyName, extractor);
    }

    private PropertyMatcherFactory(String propertyName, Function<T, U> extractor) {
        this.extractor = extractor;
        this.propertyName = propertyName;
    }

    /**
     * Returns a {@link Matcher} matching objects of the type {@link <T>} for
     * which the property extracted by this instance matches the given matcher.
     */
    public Matcher<T> matches(Matcher<? super U> subMatcher) {
        return new FeatureMatcher<T, U>(subMatcher, propertyName, propertyName) {

            @Override
            protected U featureValueOf(T actual) {
                return extractor.apply(actual);
            }
        };
    }

    /**
     * Returns a {@link Matcher} matching objects of the type {@link <T>} for
     * which the property extracted by this instance is equal to the given
     * value.
     */
    public Matcher<T> equalTo(U expected) {
        return matches(Matchers.equalTo(expected));
    }

    /**
     * A synonym for {@link #equalTo} to be used when the assertion is then
     * easier to read.
     */
    public Matcher<T> is(U expected) {
        return equalTo(expected);
    }
}
