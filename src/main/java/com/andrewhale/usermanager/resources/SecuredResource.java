package com.andrewhale.usermanager.resources;

import com.andrewhale.usermanager.api.User;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.auth.Auth;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.security.Principal;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

/**
 * Created by andrew on 7/1/2016.
 */
@Path("/jwt")
@Produces(APPLICATION_JSON)
public class SecuredResource {
    private final byte[] token;

    public SecuredResource(byte[] token) {
        this.token = token;
    }

    @GET
    @Path("/generate-expired-token")
    public Map<String, String> generateExpiredToken() {
        JwtClaims claims = new JwtClaims();
        claims.setExpirationTimeMinutesInTheFuture(-20);
        claims.setSubject("good-guy");

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(token));

        try {
            return singletonMap("token", jws.getCompactSerialization());
        } catch (JoseException e) {
            throw Throwables.propagate(e);
        }
    }

    @GET
    @Path("/generate-valid-token")
    public Map<String, String> generateValidToken() {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject("good-guy");
        claims.setExpirationTimeMinutesInTheFuture(30);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(token));

        try {
            return singletonMap("token", jws.getCompactSerialization());
        } catch (JoseException e) {
            throw Throwables.propagate(e);
        }
    }

    @GET
    @Path("/generate-invalid-token")
    public Map<String, String> generateInValidToken() {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject("bad-guy");
        claims.setExpirationTimeMinutesInTheFuture(30);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(token));

        try {
            return singletonMap("token", jws.getCompactSerialization());
        } catch (JoseException e) {
            throw Throwables.propagate(e);
        }
    }

    @GET
    @Path("/check-token")
    public Map<String, Object> get(@Auth Principal user) {
        return ImmutableMap.<String, Object>of("username", user.getName(), "id", ((User) user).getUserId());
    }

}
