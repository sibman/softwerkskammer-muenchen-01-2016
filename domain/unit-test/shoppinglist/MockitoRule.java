package shoppinglist;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * A JUnit {@link TestRule} which triggers the automatic injection
 * {@link Mockito} mocks into fields annotated with {@link Mock} in the test
 * instance, as well as {@link ArgumentCaptor} instances into fields annotated
 * with {@link Captor}.
 * 
 * <p>
 * To use it, add the following to the test class:
 * {@code @Rule public final MockitoRule mocks = MockitoRule.forTest(this);}
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public class MockitoRule implements TestRule {

    private final Object testInstance;

    /**
     * Constructs an instance of this rule for the given test class instance.
     */
    public static MockitoRule forTest(Object testInstance) {
        return new MockitoRule(testInstance);
    }

    private MockitoRule(Object testInstance) {
        this.testInstance = testInstance;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(testInstance);
                base.evaluate();
            }
        };
    }
}
