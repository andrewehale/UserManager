package com.andrewhale.usermanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 6/21/2016.
 */
public class UserTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJSON() throws Exception {

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);

        System.out.println("serializeToJSON");
        final User user = new User(Long.MAX_VALUE, "test@testdomain.com");
        System.out.println(MAPPER.writeValueAsString(user));
        assertThat(MAPPER.writeValueAsString(user)).isEqualTo(fixture("fixtures/user.json"));

    }

    @Test
    public void deserializesFromJSON() throws Exception {
        System.out.println("deserializeFromJSON");
        final User user = new User(Long.MAX_VALUE, "test@testdomain.com");
        System.out.println(user);
        System.out.println(MAPPER.readValue(fixture("fixtures/user.json"), User.class).toString());
        assertThat(MAPPER.readValue(fixture("fixtures/user.json"), User.class)).isEqualTo(user);
    }
}