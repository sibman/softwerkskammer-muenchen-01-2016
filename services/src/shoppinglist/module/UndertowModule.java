package shoppinglist.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

/**
 * Guice module to provide instances of {@link Undertow}, the embedded web
 * server used by this application.
 * 
 * @author Bradford Hovinen <hovinen@gmail.com>
 */
public final class UndertowModule extends AbstractModule {

    @Override
    protected void configure() {}

    @Provides
    @Singleton
    Undertow getUndertow(PathHandler pathHandler) {
        return Undertow.builder()
                .setHandler(pathHandler)
                .addHttpListener(8080, "0.0.0.0").build();
    }
}
