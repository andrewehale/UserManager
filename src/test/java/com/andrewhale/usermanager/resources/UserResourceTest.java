package com.andrewhale.usermanager.resources;

import com.andrewhale.usermanager.Err;
import com.andrewhale.usermanager.Tools;
import com.andrewhale.usermanager.api.NewUserToken;
import com.andrewhale.usermanager.api.Status;
import com.andrewhale.usermanager.api.User;
import com.andrewhale.usermanager.db.UsersDAO;
import com.google.common.collect.ImmutableList;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 * Created by andrew on 6/21/2016.
 */
public class UserResourceTest {

    private static final UsersDAO mockUsersDao = mock(UsersDAO.class);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .addResource(new UserResource(mockUsersDao))
            .build();


    @After
    public void tearDown() {
        reset(mockUsersDao);
    }

    @Test
    public void testDoGetList() {
        User user = new User(1000L, "test@testdomain.com", "First Last");
        final List<User> users = ImmutableList.of(user);
        when(mockUsersDao.selectAllUsers()).thenReturn(users);
        final List<User> result = RULE.client().target("/users/list").request().get(new GenericType<List<User>>() {
        });

        verify(mockUsersDao).selectAllUsers();

        assertThat(result).containsAll(users);
    }

    @Test
    public void testDoPostUserAdd() {
        NewUserToken user = new NewUserToken("test@testdomain.com", "password10", "First Last");

        final Response response = RULE.client().target("/users/add").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        Status status = response.readEntity(Status.class);
        assertThat(status.getErrorCode()).isEqualTo(0);

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[256];
        random.nextBytes(salt);

        byte[] saltCheck = new byte[256];
        for (int i = 0; i < saltCheck.length; i++) {
            saltCheck[i] = (byte)0;
        }

        byte[] hashed = Tools.hashPassword(user.getPassword().toCharArray(), salt, 20, 256);

        verify(mockUsersDao).addUser(eq(user.getEmailAddress()), anyObject(), eq(user.getName()), anyObject());
    }

    /**
     * Invalid email tests. Obviously this is minimal, we are relying on commons-validator and tools unit tests
     */
    @Test
    public void testDoPostUserAddInvalidEmail() {
        NewUserToken user = new NewUserToken("invalid_email", "password", "First Last");

        final Response response = RULE.client().target("/users/add").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        Status status = response.readEntity(Status.class);
        assertThat(status.getErrorCode()).isEqualTo(Err.INVALID_EMAIL);

    }

    /**
     * Invalid password. Obviously this is minimal. We are relying on tools unit tests to test password validation
     */
    @Test
    public void testDoPostUserAddInvalidPass() {
        NewUserToken user = new NewUserToken("test@testdomain.com", "password", "First Last");

        final Response response = RULE.client().target("/users/add").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        Status status = response.readEntity(Status.class);
        assertThat(status.getErrorCode()).isEqualTo(Err.INVALID_PASS);

    }


}