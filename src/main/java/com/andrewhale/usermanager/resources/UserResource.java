package com.andrewhale.usermanager.resources;

import com.andrewhale.usermanager.Err;
import com.andrewhale.usermanager.Tools;
import com.andrewhale.usermanager.api.Status;
import com.andrewhale.usermanager.api.User;
import com.andrewhale.usermanager.api.UserWithPassword;
import com.andrewhale.usermanager.db.UsersDAO;
import com.codahale.metrics.annotation.Timed;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by andrew on 6/21/2016.
 */
@Path("/users/{action}/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final Logger log = LoggerFactory.getLogger("com.andrewhale.usermanager");

    private final UsersDAO usersDao;

    public UserResource(UsersDAO usersDao) {
        this.usersDao = usersDao;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Object doPost(@PathParam("action") String action, @PathParam("id") long userId, UserWithPassword userWithPassword) {
        Status status = new Status();
        log.info(String.format("doPost - action: %s, id: %s", action, Long.toString(userId)));

        switch (action) {
            case "add":
                status = addUser(userWithPassword);
                break;
            default:
                status.setErrorCode(Err.UNEXPECTED_ACTION);
                status.setErrorMessage("Unexpected action: " + action);
                break;
        }

        return status;
    }

    @GET
    @Timed
    public Object doGet(@PathParam("action") String action, @PathParam("id") long userId) {
        Status status = new Status();
        log.info(String.format("doGet - action: %s, id: %s", action, Long.toString(userId)));

        switch (action) {
            case "list":
                return doList();
            default:
                status.setErrorCode(Err.UNEXPECTED_ACTION);
                status.setErrorMessage("Unexpected action: " + action);
                return status;
        }
    }

    /**
     * This method validates a user and posts it to the database if it meets
     * user requirements. Returns a bad status if something went wrong.
     *
     * Synchronized so we don't get into any issues with race conditions and adding users
     * @param userWithPassword
     * @return Status object. Check code and message for details.
     */
    public  synchronized Status addUser(UserWithPassword userWithPassword) {
        Status status = new Status();
        log.info("addUser");

        if (usersDao.selectUserByEmailAddress(userWithPassword.getEmailAddress()) != null) {
            status.setErrorCode(Err.USER_EXISTS);
            status.setErrorMessage(String.format("A user already exists with the email address %s", userWithPassword.getEmailAddress()));
        }

        if (status.isGood()) {
            status = Tools.validateEmailAddress(userWithPassword.getEmailAddress());
        }

        if (status.isGood()) {
            status = Tools.validatePasswordRequirements(userWithPassword.getPassword());
        }

        if (status.isGood()) {

            byte[] hashed = Tools.hashPassword(userWithPassword.getPassword().toCharArray(), userWithPassword.getSalt(), 20, 256);

            try {
                usersDao.addUser(userWithPassword.getEmailAddress(), hashed, userWithPassword.getSalt());
            } catch (UnableToExecuteStatementException ex) {
                status.setErrorCode(Err.SQL_ERROR);
                status.setErrorMessage(ex.getLocalizedMessage());
            }

            userWithPassword.setPassword("");
        }

        return status;
    }

    /**
     * Just returns all objects from the dao
     *
     * @return A list of User objects
     */
    public List<User> doList() {
        return usersDao.selectAllUsers();
    }
}
