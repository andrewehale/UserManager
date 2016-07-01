package com.andrewhale.usermanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 6/21/2016.
 */
public class NewUserTokenTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJSON() throws Exception {

        System.out.println("serializeToJSON");
        final NewUserToken user = new NewUserToken("test@testdomain.com", "password", "First Last");
        String json = MAPPER.writeValueAsString(user);
        assertThat(MAPPER.writeValueAsString(user)).isEqualTo(fixture("fixtures/auth_token.json"));
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        System.out.println("deserializeFromJSON");
        final NewUserToken user = new NewUserToken("test@testdomain.com", "password", "First Last");
        assertThat(MAPPER.readValue(fixture("fixtures/auth_token.json"), NewUserToken.class)).isEqualTo(user);
    }
}