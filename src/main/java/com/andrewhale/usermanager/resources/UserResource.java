package com.andrewhale.usermanager.resources;

import com.andrewhale.usermanager.Err;
import com.andrewhale.usermanager.Tools;
import com.andrewhale.usermanager.api.NewUserToken;
import com.andrewhale.usermanager.api.Status;
import com.andrewhale.usermanager.api.User;
import com.andrewhale.usermanager.db.UsersDAO;
import com.codahale.metrics.annotation.Timed;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by andrew on 6/21/2016.
 */
@Path("/users/{action}")
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
    public Object doPost(@PathParam("action") String action, NewUserToken newUserToken) {
        Status status = new Status();
        log.info(String.format("doPost - action: %s", action));

        switch (action) {
            case "add":
                status = addUser(newUserToken);
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
    //TODO add auth level admin
    public Object doGet(@PathParam("action") String action) {
        Status status = new Status();
        log.info(String.format("doGet - action: %s", action));

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
     * @param newUserToken
     * @return Status object. Check code and message for details.
     */
    public  synchronized Status addUser(NewUserToken newUserToken) {
        Status status = new Status();
        log.info("addUser");

        if (usersDao.selectUserByEmailAddress(newUserToken.getEmailAddress()) != null) {
            status.setErrorCode(Err.USER_EXISTS);
            status.setErrorMessage(String.format("A user already exists with the email address %s", newUserToken.getEmailAddress()));
        }

        if (status.isGood()) {
            status = Tools.validateEmailAddress(newUserToken.getEmailAddress());
        }

        if (status.isGood()) {
            status = Tools.validatePasswordRequirements(newUserToken.getPassword());
        }

        if (status.isGood()) {

            // generate unique salt and store it on the server
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[256];
            random.nextBytes(salt);

            byte[] hashed = Tools.hashPassword(newUserToken.getPassword().toCharArray(), salt, 20, 256);

            // TODO need to move this all into a transaction so I can also add permissions

            try {
                usersDao.addUser(newUserToken.getEmailAddress(), hashed, newUserToken.getName(), salt);
            } catch (UnableToExecuteStatementException ex) {
                status.setErrorCode(Err.SQL_ERROR);
                status.setErrorMessage(ex.getLocalizedMessage());
            }

            for (int i = 0; i < salt.length; i++) {
                salt[i] = (byte)0;
            }

            newUserToken.setPassword("");
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
