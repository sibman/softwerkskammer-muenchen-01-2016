package shoppinglist.module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import javax.servlet.ServletException;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import io.undertow.Handlers;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import shoppinglist.services.ShoppingListClientNotifier;
import shoppinglist.services.ShoppingListService;
import shoppinglist.services.ShoppingListSubscriptionService;

/**
 * A Guice module to configure the deployment of servlets in the embedded web
 * container.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class DeploymentModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ShoppingListService.class).in(Scopes.SINGLETON);
        bind(ShoppingListSubscriptionService.class).in(Scopes.SINGLETON);
        bind(ExecutorService.class).to(ForkJoinPool.class);
        bind(ShoppingListClientNotifier.Factory.class)
        .to(ShoppingListClientNotifier.ConcreteFactory.class);
        bind(ResourceConfig.class).to(ShoppingListServicesConfig.class);
    }

    @Provides
    @Singleton
    PathHandler getPathManager(
            DeploymentManager manager,
            WebSocketProtocolHandshakeHandler websocketHandler) throws ServletException {
        return new PathHandler()
                .addPrefixPath("/services/shopping-list/subscribe", websocketHandler)
                .addPrefixPath("/", manager.start());
    }

    @Provides
    @Singleton
    WebSocketProtocolHandshakeHandler websocketHandler(
            ShoppingListSubscriptionService service) {
        return Handlers.websocket(service);
    }

    @Provides
    @Singleton
    DeploymentManager createDeploymentManager(DeploymentInfo deploymentInfo) {
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        manager.deploy();
        return manager;
    }

    @Provides
    @Singleton
    DeploymentInfo createDeploymentInfo(
            Provider<ShoppingListSubscriptionService> shoppingListServlet,
            Provider<ResourceConfig> resourceConfig) {
        return Servlets.deployment()
                .setDeploymentName("root")
                .setContextPath("/")
                .setClassLoader(getClass().getClassLoader())
                .addServlet(createJerseyServlet(resourceConfig));
    }

    private ServletInfo createJerseyServlet(Provider<ResourceConfig> config) {
        return Servlets.servlet(getClass().getCanonicalName(), ServletContainer.class,
                new InstanceFactory<ServletContainer>() {

            @Override
            public InstanceHandle<ServletContainer> createInstance()
                    throws InstantiationException {
                ServletContainer servletContainer = new ServletContainer(config.get());
                return new InstanceHandle<ServletContainer>() {

                    @Override
                    public ServletContainer getInstance() {
                        return servletContainer;
                    }

                    @Override
                    public void release() {
                        servletContainer.destroy();
                    }
                };
            }
        }).addMapping("/services/*").setAsyncSupported(true);
    }
}
