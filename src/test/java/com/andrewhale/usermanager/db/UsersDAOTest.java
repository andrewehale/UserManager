package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.EncryptedUser;
import com.andrewhale.usermanager.api.User;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;


/**
 * Created by andrew on 6/21/2016.
 */
public class UsersDAOTest {
    @ClassRule
    public static final H2JDBIRule server = new H2JDBIRule();

    public final UsersDAO instance;

    public UsersDAOTest() {
        instance = server.getDbi().onDemand(UsersDAO.class);
    }

    @Before
    public void clearUsersTable() {
        System.out.println("clearUsersTable");
        instance.deleteAllUsers();

    }

    @Test
    public void testAddUser() {
        System.out.println("addUser");

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[256];
        random.nextBytes(salt);

        instance.addUser("test@testdomain.com", "password".getBytes(), salt);

        User test = new User(1000L, "test@testdomain.com");
        List<User> results = instance.selectAllUsers();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmailAddress()).isEqualTo("test@testdomain.com");
    }

    @Test
    public void testAddDuplicate() {
        System.out.println("addDuplicateUser");
        instance.addUser("test@testdomain.com", "password".getBytes(), "salt".getBytes());

        User test = new User(1000L, "test@testdomain.com");
        List<User> results = instance.selectAllUsers();
        assertThat(results.get(0).getEmailAddress()).isEqualTo("test@testdomain.com");

        try {
            instance.addUser("test@testdomain.com", "password".getBytes(), "salt".getBytes());
            fail("Expected exception not thrown.");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Unique index or primary key violation");
        }
    }

    @Test
    public void testDeleteAllUsers() {
        System.out.println("testDeleteAllUsers");
        instance.addUser("test@testdomain.com", "password".getBytes(), "salt".getBytes());
        instance.addUser("test1@testdomain.com", "password".getBytes(), "salt".getBytes());
        instance.addUser("test2@testdomain.com", "password".getBytes(), "salt".getBytes());

        List<User> results = instance.selectAllUsers();
        assertThat(results).hasSize(3);

        instance.deleteAllUsers();
        results = instance.selectAllUsers();
        assertThat(results).hasSize(0);
    }

    @Test
    public void testSelectAllUsers() {
        System.out.println("testSelectAllUsers");
        instance.addUser("test@testdomain.com", "password".getBytes(), "salt".getBytes());
        instance.addUser("test1@testdomain.com", "password".getBytes(), "salt".getBytes());
        instance.addUser("test2@testdomain.com", "password".getBytes(), "salt".getBytes());

        List<User> results = instance.selectAllUsers();
        assertThat(results).hasSize(3);
        assertThat(results.get(0).getEmailAddress()).isEqualTo("test@testdomain.com");
        assertThat(results.get(1).getEmailAddress()).isEqualTo("test1@testdomain.com");
        assertThat(results.get(2).getEmailAddress()).isEqualTo("test2@testdomain.com");
    }

    @Test
    public void testAutoIncrementUserId() {
        System.out.println("testAutoIncrementUserId");
        instance.addUser("test@testdomain.com", "password".getBytes(), "salt".getBytes());
        instance.addUser("test1@testdomain.com", "password".getBytes(), "salt".getBytes());
        instance.addUser("test2@testdomain.com", "password".getBytes(), "salt".getBytes());

        List<User> results = instance.selectAllUsers();
        assertThat(results).hasSize(3);
        assertThat(results.get(0).getUserId()).isEqualTo(results.get(1).getUserId() - 1).isEqualTo(results.get(2).getUserId() - 2);
        assertThat(results.get(1).getUserId()).isEqualTo(results.get(0).getUserId() + 1).isEqualTo(results.get(2).getUserId() - 1);
        assertThat(results.get(2).getUserId()).isEqualTo(results.get(1).getUserId() + 1).isEqualTo(results.get(0).getUserId() + 2);
    }

    @Test
    public void testSelectUserByUserId() {
        System.out.println("testSelectUserByUserId");
        instance.addUser("search@testdomain.com", "password".getBytes(), "salt".getBytes());
        List<User> results = instance.selectAllUsers();
        assertThat(results).hasSize(1);
        long userIdSearchKey = results.get(0).getUserId();

        // insert some more data just so there is something in the table
        for (int i = 0; i < 100; i++) {
            instance.addUser(String.format("test%s@testdomain.com", Integer.toString(i)), "password".getBytes(), "salt".getBytes());
        }

        results = instance.selectAllUsers();
        assertThat(results).hasSize(101);

        User searchResult = instance.selectUserByUserId(userIdSearchKey);
        assertThat(searchResult.getEmailAddress()).isEqualTo("search@testdomain.com");
        assertThat(searchResult.getUserId()).isEqualTo(userIdSearchKey);
    }

    @Test
    public void testSelectUserByUserIdInvalid() {
        System.out.println("testSelectUserByUserIdInvalid");
        instance.addUser("search@testdomain.com", "password".getBytes(), "salt".getBytes());

        User searchResult = instance.selectUserByUserId(-1L);
        assertThat(searchResult).isNull();

    }

    @Test
    public void testSelectUserByEmailAddress() {
        System.out.println("testSelectUserByEmailAddress");
        instance.addUser("search@testdomain.com", "password".getBytes(), "salt".getBytes());
        List<User> results = instance.selectAllUsers();
        assertThat(results).hasSize(1);
        long userIdSearchKey = results.get(0).getUserId();

        // insert some more data just so there is something in the table
        for (int i = 0; i < 100; i++) {
            instance.addUser(String.format("test%s@testdomain.com", Integer.toString(i)), "password".getBytes(), "salt".getBytes());
        }

        results = instance.selectAllUsers();
        assertThat(results).hasSize(101);

        User searchResult = instance.selectUserByEmailAddress("search@testdomain.com");
        assertThat(searchResult.getEmailAddress()).isEqualTo("search@testdomain.com");
        assertThat(searchResult.getUserId()).isEqualTo(userIdSearchKey);
    }

    @Test
    public void testSelectUserByEmailAddressInvalid() {
        System.out.println("testSelectUserByEmailAddressInvalid");
        instance.addUser("search@testdomain.com", "password".getBytes(), "salt".getBytes());

        User searchResult = instance.selectUserByEmailAddress("");
        assertThat(searchResult).isNull();

    }

    @Test
    public void testSelectUserWithPassByEmailAddress() {
        System.out.println("testSelectUserWithpassByEmailAddress");

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[256];
        random.nextBytes(salt);

        instance.addUser("test@testdomain.com", "password".getBytes(), salt);

        EncryptedUser testUser = instance.selectUserWithPassByEmailAddress("test@testdomain.com");
        assertThat(testUser.getSalt()).isEqualTo(salt);
        assertThat(testUser.getPassword()).isEqualTo("password".getBytes());
    }
}