package com.andrewhale.usermanager.db;

import com.andrewhale.usermanager.api.EncryptedUser;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by andrew on 6/21/2016.
 */
public class EncryptedUserMapper implements ResultSetMapper<EncryptedUser>  {
    @Override
    public EncryptedUser map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new EncryptedUser(resultSet.getLong("USER_ID"),
                resultSet.getString("EMAIL_ADDRESS"),
                resultSet.getBytes("PASSWORD"),
                resultSet.getBytes("SALT"));

        //TODO probably should just make user with pass a plain pojo and move the encryption stuff because it is
        //making it a pain to serialize/deserialize as well as map fields
    }
}
