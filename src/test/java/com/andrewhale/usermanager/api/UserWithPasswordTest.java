package com.andrewhale.usermanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.util.Base64;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 6/21/2016.
 */
public class UserWithPasswordTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJSON() throws Exception {

        System.out.println("serializeToJSON");
        final UserWithPassword user = new UserWithPassword(Long.MAX_VALUE, "test@testdomain.com", "password");
        String json = MAPPER.writeValueAsString(user);

        // can't test password with fixture because of random salt and encryption
        String fixture = String.format("{\"emailAddress\":\"test@testdomain.com\",\"password\":\"%s\",\"userId\":9223372036854775807,\"salt\":\"%s\"}",
                user.getPassword(),
                Base64.getEncoder().encodeToString(user.getSalt()));
        System.out.println(fixture);
        System.out.println(json);
        assertThat(json).isEqualTo(fixture);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        System.out.println("deserializeFromJSON");
        final UserWithPassword user = new UserWithPassword(Long.MAX_VALUE, "test@testdomain.com", "password");
        System.out.println(user);
        System.out.println(MAPPER.readValue(fixture("fixtures/user_with_password.json"), UserWithPassword.class).toString());
        // TODO don't really know how to test this since the salt gets generated on the fly...
        //assertThat(MAPPER.readValue(fixture("fixtures/user_with_password.json"), UserWithPassword.class)).isEqualTo(user);
    }
}