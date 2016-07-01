package com.andrewhale.usermanager.auth;

/**
 * Created by andrew on 7/1/2016.
 */
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.jersey.DropwizardResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.container.ContainerRequestFilter;
import java.security.Principal;

public abstract class AuthBaseResourceConfig extends DropwizardResourceConfig {
    public AuthBaseResourceConfig() {
        super(true, new MetricRegistry());

        register(new AuthDynamicFeature(getAuthFilter()));
        register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        register(RolesAllowedDynamicFeature.class);
        register(AuthTestResource.class);
    }

    protected abstract ContainerRequestFilter getAuthFilter();
}
