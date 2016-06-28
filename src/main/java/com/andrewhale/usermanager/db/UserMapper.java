package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by andrew on 6/21/2016.
 */
public class UserMapper implements ResultSetMapper<User>  {
    @Override
    public User map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new User(resultSet.getLong("USER_ID"),
                resultSet.getString("EMAIL_ADDRESS"));
    }
}
