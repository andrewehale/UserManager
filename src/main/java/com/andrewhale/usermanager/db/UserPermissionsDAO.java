package com.andrewhale.usermanager.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

/**
 * Created by andrew on 7/1/2016.
 */
public abstract class UserPermissionsDAO {
    @SqlQuery("select p.PERMISSION_NAME from PERMISSIONS p, USER_PERMISSIONS up where p.PERMISSION_ID = up.PERMISSION_ID AND up.USER_ID = :user_id")
    public abstract List<String> getRolesForUser(@Bind("user_id") long userId);

    @SqlUpdate("insert into USER_PERMISSIONS(USER_ID, PERMISSION_ID) values(:user_id, :permission_id)")
    public abstract void addRoleForUser(@Bind("user_id") long userId, @Bind("permission_id") int permissionId);


}
