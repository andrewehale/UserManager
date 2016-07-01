package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.Permission;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by andrew on 7/1/2016.
 */
public class PermissionsDAOTest {
    @ClassRule
    public static final H2JDBIRule server = new H2JDBIRule();

    public final PermissionsDAO instance;

    public PermissionsDAOTest() {
        instance = server.getDbi().onDemand(PermissionsDAO.class);
    }

    @Before
    public void clearPermissionTable() {
        System.out.println("clearPermissionsTable");

    }

    @Test
    public void testAddPermissionAndSelectAllAndDefaults() {
        System.out.println("testAddPermissionAndSelectAllAndDefaults");

        instance.addPermision("NEW");
        Permission newRole = new Permission(2, "NEW");
        Permission admin = new Permission(0, "ADMIN");
        Permission user = new Permission(1, "USER");

        List<Permission> results = instance.selectAllPermisions();
        assertThat(results).hasSize(3);
        assertThat(results).contains(newRole).contains(admin).contains(user);
    }
}
