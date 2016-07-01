package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.Permission;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by andrew on 7/1/2016.
 */
public class PermissionMapper implements ResultSetMapper<Permission> {
    @Override
    public Permission map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new Permission(resultSet.getInt("PERMISSION_ID"),
                resultSet.getString("PERMISSION_NAME"));
    }
}

