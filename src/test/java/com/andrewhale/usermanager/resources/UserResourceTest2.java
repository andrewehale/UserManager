package com.andrewhale.usermanager.resources;

import com.andrewhale.usermanager.Err;
import com.andrewhale.usermanager.api.NewUserToken;
import com.andrewhale.usermanager.api.Status;
import com.andrewhale.usermanager.db.H2JDBIRule;
import com.andrewhale.usermanager.db.UsersDAO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 6/24/2016.
 */
public class UserResourceTest2 {

    @ClassRule
    public static final H2JDBIRule server = new H2JDBIRule();
    public final UsersDAO instance;
    @Rule
    public final ResourceTestRule RULE;

    public UserResourceTest2() {
        instance = server.getDbi().onDemand(UsersDAO.class);
        RULE = ResourceTestRule.builder()
                .addResource(new UserResource(instance))
                .build();
    }

    /**
     * Test duplicate entries -- need to actually spin up our data source here, might be a little slow, probably a better
     * way?
     */
    @Test
    public void testDoPostUserAddDuplicates() {
        // Now add the first user and verify it happened
        NewUserToken user = new NewUserToken("test@testdomain.com", "password10", "First Last");

        Response response = RULE.client().target("/users/add").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        Status status = response.readEntity(Status.class);
        assertThat(status.getErrorCode()).isEqualTo(0);

        NewUserToken sameUser = new NewUserToken("test@testdomain.com", "password11", "First Last");
        response = RULE.client().target("/users/add").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(sameUser, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        status = response.readEntity(Status.class);
        assertThat(status.getErrorCode()).isEqualTo(Err.USER_EXISTS);

        // make sure only one entry
        assertThat(instance.selectAllUsers().size()).isEqualTo(1);
    }
}
