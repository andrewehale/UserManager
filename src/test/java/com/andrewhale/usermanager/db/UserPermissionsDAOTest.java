package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.User;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 7/1/2016.
 */
public class UserPermissionsDAOTest {
    @ClassRule
    public static final H2JDBIRule server = new H2JDBIRule();

    public final UserPermissionsDAO instance;
    public final UsersDAO usersInstance;

    public UserPermissionsDAOTest() {
        usersInstance = server.getDbi().onDemand(UsersDAO.class);
        instance = server.getDbi().onDemand(UserPermissionsDAO.class);
    }

    @Before
    public void clearPermissionTable() {
        System.out.println("clearPermissionsTable");

    }

    @Test
    public void testGetRolesForUser() {
        System.out.println("testGetUserPermissions");
        usersInstance.addUser("test@domain.com", "password".getBytes(), "Test User", "salt".getBytes());
        User testUser = usersInstance.selectUserByEmailAddress("test@domain.com");
        List<String> userRoles = instance.getRolesForUser(testUser.getUserId());
        assertThat(userRoles).isEmpty();

        // I just know the default roles are ADMIN - 0 and USER 1
        instance.addRoleForUser(testUser.getUserId(), 0);
        instance.addRoleForUser(testUser.getUserId(), 1);
        userRoles = instance.getRolesForUser(testUser.getUserId());
        assertThat(userRoles).hasSize(2).contains("ADMIN", "USER").doesNotContain("TEST");
    }

    // TODO test unique constraint
}
