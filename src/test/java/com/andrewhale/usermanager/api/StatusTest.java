package com.andrewhale.usermanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author andrew
 */
public class StatusTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJSON() throws Exception {
        System.out.println("serializeToJSON");
        final Status status = new Status();
        System.out.println(MAPPER.writeValueAsString(status));
        assertThat(MAPPER.writeValueAsString(status)).isEqualTo(fixture("fixtures/status.json"));
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        System.out.println("deserializeFromJSON");
        final Status status = new Status();
        System.out.println(status);
        System.out.println(MAPPER.readValue(fixture("fixtures/status.json"), Status.class).toString());
        assertThat(MAPPER.readValue(fixture("fixtures/status.json"), Status.class)).isEqualTo(status);
    }

}