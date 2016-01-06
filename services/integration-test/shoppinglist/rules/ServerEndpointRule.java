package shoppinglist.rules;

import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.jayway.restassured.RestAssured;

import io.undertow.Undertow;
import shoppinglist.module.DeploymentModule;
import shoppinglist.module.UndertowModule;

/**
 * A {@link TestRule} which launches a version of the backend in order to run
 * integration tests against its REST interface.
 * 
 * <p>
 * The dependency injection for instance launched with this rule can be
 * configured with the methods {@link #withBinding(Class, Class)},
 * {@link #withBinding(Class, Object)}, and {@link #withDummyBinding(Class)}.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class ServerEndpointRule implements TestRule {

    private final Set<InstanceBinding<?>> instanceBindings = new HashSet<>();
    private final Set<ClassBinding<?>> classBindings = new HashSet<>();

    /**
     * Creates an instance of this rule.
     */
    public static ServerEndpointRule rule() {
        return new ServerEndpointRule();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                RestAssured.baseURI = "http://localhost";
                RestAssured.port = 8080;
                RestAssured.authentication = RestAssured.basic("admin", "admin");
                Undertow undertow = Guice
                        .createInjector(
                                new UndertowModule(), new DeploymentModule(), new BindingsModule())
                        .getInstance(Undertow.class);
                undertow.start();
                try {
                    base.evaluate();
                } finally {
                    undertow.stop();
                }
            }
        };
    }

    /**
     * Configures the Guice module for the server instance to bind the given
     * class to the given concrete instance.
     * 
     * @return this rule for chaining
     */
    public <T> ServerEndpointRule withBinding(Class<? super T> targetClass, T instance) {
        replace(instanceBindings, new InstanceBinding<>(targetClass, instance));
        return this;
    }

    /**
     * Configures the Guice module for the server instance to bind the given
     * class to the given subclass thereof.
     * 
     * @return this rule for chaining
     */
    public <T> ServerEndpointRule withBinding(Class<? super T> targetClass,
            Class<T> instanceClass) {
        replace(classBindings, new ClassBinding<>(targetClass, instanceClass));
        return this;
    }

    /**
     * Configures the Guice module for the server instance to bind the given
     * class to a {@link Mockito} mock to be used as a dummy object.
     * 
     * @return this rule for chaining
     */
    public <T> ServerEndpointRule withDummyBinding(Class<T> targetClass) {
        replace(instanceBindings, new InstanceBinding<T>(targetClass, mock(targetClass)));
        return this;
    }

    private <T> void replace(Set<T> set, T element) {
        set.remove(element);
        set.add(element);
    }

    private class BindingsModule extends AbstractModule {

        @Override
        protected void configure() {
            for (ClassBinding<?> classBinding : classBindings) {
                bindClass(classBinding);
            }
            for (InstanceBinding<?> instanceBinding : instanceBindings) {
                bindInstance(instanceBinding);
            }
        }

        private <T> void bindClass(ClassBinding<T> classBinding) {
            bind(classBinding.targetClass).to(classBinding.instanceClass);
        }

        private <T> void bindInstance(InstanceBinding<T> instanceBinding) {
            bind(instanceBinding.targetClass).toInstance(instanceBinding.instance);
        }
    }

    private static class Binding<T> {
        public final Class<? super T> targetClass;

        public Binding(Class<? super T> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InstanceBinding) {
                InstanceBinding<?> other = (InstanceBinding<?>) obj;
                return targetClass == other.targetClass;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetClass);
        }
    }

    private static class InstanceBinding<T> extends Binding<T> {
        public final T instance;

        public InstanceBinding(Class<? super T> targetClass, T instance) {
            super(targetClass);
            this.instance = instance;
        }

    }

    private static class ClassBinding<T> extends Binding<T> {
        public final Class<T> instanceClass;

        public ClassBinding(Class<? super T> targetClass, Class<T> instanceClass) {
            super(targetClass);
            this.instanceClass = instanceClass;
        }
    }
}
