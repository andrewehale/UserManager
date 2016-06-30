package com.andrewhale.usermanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 6/21/2016.
 */
public class AuthTokenTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJSON() throws Exception {

        System.out.println("serializeToJSON");
        final AuthToken user = new AuthToken("test@testdomain.com", "password");
        String json = MAPPER.writeValueAsString(user);
        assertThat(MAPPER.writeValueAsString(user)).isEqualTo(fixture("fixtures/auth_token.json"));
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        System.out.println("deserializeFromJSON");
        final AuthToken user = new AuthToken("test@testdomain.com", "password");
        assertThat(MAPPER.readValue(fixture("fixtures/auth_token.json"), AuthToken.class)).isEqualTo(user);
    }
}