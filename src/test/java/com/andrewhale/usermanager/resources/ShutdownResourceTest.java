/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrewhale.usermanager.resources;

import com.andrewhale.usermanager.api.Status;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 *
 * @author andrew
 */
public class ShutdownResourceTest {

    private static final Runnable stopJettyFunction = mock(Runnable.class);
    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .addResource(new ShutdownResource(stopJettyFunction))
            .build();

    public ShutdownResourceTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        reset(stopJettyFunction);
    }

    /**
     * Test of shutdownServer method, of class ShutdownResource.
     */
    @Test
    public void testShutdownServer() {
        System.out.println("shutdownServer");
        Status status = RULE.client().target("/shutdown_server").request().get(Status.class);
        verify(stopJettyFunction).run();
        assertThat(status.getErrorCode()).isEqualTo(0);
    }

}
