package com.andrewhale.usermanager.db;

import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andrew on 7/1/2016.
 */
public abstract class AddUserRepository {
    private final Logger log = LoggerFactory.getLogger("com.andrewhale.usermanager");

    @CreateSqlObject
    abstract UsersDAO usersDao();

    @CreateSqlObject
    abstract UserPermissionsDAO userPermissionsDao();

    @CreateSqlObject
    abstract PermissionsDAO permissionsDao();


}
