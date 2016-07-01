package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.EncryptedUser;
import com.andrewhale.usermanager.api.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by andrew on 6/21/2016.
 */
public abstract class UsersDAO {

    @SqlUpdate("insert into USERS (EMAIL_ADDRESS, PASSWORD, NAME, SALT) values(:emailAddress, :password, :name, :salt)")
    public abstract void addUser(@Bind("emailAddress") String emailAddress,
                                 @Bind("password") byte[] password,
                                 @Bind("name") String name,
                                 @Bind("salt") byte[] salt);

    @SqlUpdate("delete from USERS")
    public abstract void deleteAllUsers();

    @RegisterMapper(UserMapper.class)
    @SqlQuery("select USER_ID, EMAIL_ADDRESS, NAME from USERS")
    public abstract List<User> selectAllUsers();

    @RegisterMapper(UserMapper.class)
    @SqlQuery("select USER_ID, EMAIL_ADDRESS, NAME from USERS where USER_ID = :userId")
    public abstract User selectUserByUserId(@Bind("userId") long userId);

    @RegisterMapper(UserMapper.class)
    @SqlQuery("select USER_ID, EMAIL_ADDRESS, NAME from USERS where EMAIL_ADDRESS = :emailAddress")
    public abstract User selectUserByEmailAddress(@Bind("emailAddress") String emailAddress);

    @RegisterMapper(EncryptedUserMapper.class)
    @SqlQuery("select USER_ID, EMAIL_ADDRESS, PASSWORD, NAME, SALT from USERS where EMAIL_ADDRESS = :emailAddress")
    public abstract EncryptedUser selectUserWithPassByEmailAddress(@Bind("emailAddress") String emailAddress);
}
