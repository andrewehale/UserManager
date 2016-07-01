package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.Permission;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by andrew on 7/1/2016.
 */
public abstract class PermissionsDAO {

    @SqlUpdate("insert into PERMISSIONS(PERMISSION_NAME) values(:name)")
    public abstract void addPermision(@Bind("name") String name);

    @RegisterMapper(PermissionMapper.class)
    @SqlQuery("select PERMISSION_ID, PERMISSION_NAME from PERMISSIONS")
    public abstract List<Permission> selectAllPermisions();

}
