package com.andrewhale.usermanager.auth;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheStats;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.consumer.JwtContext;

import java.security.Principal;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Predicate;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by andrew on 7/1/2016.
 */
public class TokenAuthenticator<P extends Principal> implements Authenticator<JwtContext, P> {

    private final Authenticator<JwtContext, P> authenticator;
    private final Cache<String, SimpleEntry<JwtContext, Optional<P>>> cache;
    private final Meter cacheMisses;
    private final Timer gets;

    public TokenAuthenticator(MetricRegistry metricRegistry,
                              Authenticator<JwtContext, P> authenticator,
                              CacheBuilderSpec cacheSpec) {
        this(metricRegistry, authenticator, CacheBuilder.from(cacheSpec));
    }

    public TokenAuthenticator(MetricRegistry metricRegistry,
                              Authenticator<JwtContext, P> authenticator,
                              CacheBuilder<Object, Object> builder) {
        this.authenticator = authenticator;
        this.cacheMisses = metricRegistry.meter(name(authenticator.getClass(), "cache-misses"));
        this.gets = metricRegistry.timer(name(authenticator.getClass(), "gets"));
        this.cache = builder.recordStats().build();
    }

    @Override
    public Optional<P> authenticate(JwtContext jwtContext) throws AuthenticationException {
        Timer.Context timer = gets.time();

        try {
            SimpleEntry<JwtContext, Optional<P>> cacheEntry = cache.getIfPresent(jwtContext.getJwt());
            if (cacheEntry != null) {
                return cacheEntry.getValue();
            }
            cacheMisses.mark();
            Optional<P> principal = authenticator.authenticate(jwtContext);
            if (principal.isPresent()) {
                cache.put(jwtContext.getJwt(), new SimpleEntry<JwtContext, Optional<P>>(jwtContext, principal));
            }
            return principal;
        } finally {
            timer.stop();
        }
    }

    public void invalidateAll(Iterable<JwtContext> credentials) {
        credentials.forEach(jwtContext -> cache.invalidate(jwtContext.getJwt()));
    }

    public void invalidAll(Predicate<? super JwtContext> predicate) {
        cache.asMap().entrySet().stream()
                .map(entry -> entry.getValue().getKey())
                .filter(predicate::test)
                .map(JwtContext::getJwt)
                .forEach(cache::invalidate);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    public long size() {
        return cache.size();
    }

    public CacheStats stats() {
        return cache.stats();
    }
}
