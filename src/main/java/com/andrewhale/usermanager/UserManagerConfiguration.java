package com.andrewhale.usermanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.SecureRandom;

/**
 * Created by andrew on 6/20/2016.
 */
public class UserManagerConfiguration extends Configuration{

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
        this.database.setPassword("password");
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public byte[] getTokenSecret() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[256];
        random.nextBytes(salt);

        return salt;
    }
}
