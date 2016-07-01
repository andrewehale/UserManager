package com.andrewhale.usermanager.auth;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

/**
 * Created by andrew on 7/1/2016.
 */
@Priority(Priorities.AUTHENTICATION)
public class TokenAuthFilter<P extends Principal> extends AuthFilter<JwtContext, P> {
    private final Logger log = LoggerFactory.getLogger("com.andrewhale.usermanager");

    private final JwtConsumer consumer;
    private final String cookie;

    public TokenAuthFilter(JwtConsumer consumer, String cookie) {
        this.consumer = consumer;
        this.cookie = cookie;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Optional<String> token = tryForToken(containerRequestContext);

        if (token.isPresent()) {
            try {
                JwtContext context = verifyToken(token.get());
                com.google.common.base.Optional<P> principal = authenticator.authenticate(context);

                if (principal.isPresent()) {
                    containerRequestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return principal.get();
                        }

                        @Override
                        public boolean isUserInRole(String s) {
                            return authorizer.authorize(principal.get(), s);
                        }

                        @Override
                        public boolean isSecure() {
                            return containerRequestContext.getSecurityContext().isSecure();
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return SecurityContext.BASIC_AUTH;
                        }
                    });
                    return;
                }
            } catch (InvalidJwtException ex) {
                log.warn("Error decoding credentials: " + ex.getMessage(), ex);
            } catch (AuthenticationException ex) {
                log.warn("Error authenticating credentials", ex);
                throw new InternalServerErrorException(ex);
            }
        }

        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }

    private JwtContext verifyToken(String s) throws InvalidJwtException {
        return consumer.process(s);
    }

    private Optional<String> tryForToken(ContainerRequestContext containerRequestContext) {
        Optional<String> token = getTokenFromHeader(containerRequestContext.getHeaders());

        if (token.isPresent()) {
            return token;
        }

        token = getTokenFromCookie(containerRequestContext);
        return token;

    }

    private Optional<String> getTokenFromCookie(ContainerRequestContext containerRequestContext) {
        Map<String, Cookie> cookies = containerRequestContext.getCookies();

        if (cookie != null && cookies.containsKey(cookie)) {
            Cookie tokenCookie = cookies.get(cookie);
            String rawToken = tokenCookie.getValue();
            return Optional.of(rawToken);
        }

        return Optional.empty();

    }

    private Optional<String> getTokenFromHeader(MultivaluedMap<String, String> headers) {
        String header = headers.getFirst(AUTHORIZATION);
        if (header != null) {
            int space = header.indexOf(' ');
            if (space > 0) {
                final String method = header.substring(0, space);
                if (prefix.equalsIgnoreCase(method)) {
                    final String token = header.substring(space + 1);
                    return Optional.of(token);
                }
            }
        }

        return Optional.empty();
    }

    public static class Builder<P extends Principal> extends AuthFilter.AuthFilterBuilder<JwtContext, P, TokenAuthFilter<P>> {

        private JwtConsumer consumer;
        private String cookie;

        public Builder<P> setJwtConsumer(JwtConsumer consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder<P> setCookie(String cookie) {
            this.cookie = cookie;
            return this;
        }


        @Override
        protected TokenAuthFilter<P> newInstance() {
            checkNotNull(consumer, "JwtConsumer is not set");
            return new TokenAuthFilter<>(consumer, cookie);

        }
    }
}
