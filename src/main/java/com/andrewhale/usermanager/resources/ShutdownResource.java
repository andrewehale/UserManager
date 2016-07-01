package com.andrewhale.usermanager.resources;

import com.andrewhale.usermanager.api.Status;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by andrew on 6/21/2016.
 */
@Path("/shutdown_server")
@Produces(MediaType.APPLICATION_JSON)
public class ShutdownResource {

    private static final Logger log = LoggerFactory.getLogger("com.andrewhale.usermanager");

    private final Runnable stopJettyFunction;


    public ShutdownResource(Runnable stopJettyFunction) {
        this.stopJettyFunction = stopJettyFunction;
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Status shutdownServer(@Context ServletContext context) {
        log.info("shutdownServer...");
        Status status = new Status();

        stopJettyFunction.run();

        return status;

    }
}
