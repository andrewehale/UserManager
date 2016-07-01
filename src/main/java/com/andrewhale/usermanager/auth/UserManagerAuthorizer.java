package com.andrewhale.usermanager.auth;

import io.dropwizard.auth.Authorizer;

import java.security.Principal;

/**
 * Created by andrew on 7/1/2016.
 */
public class UserManagerAuthorizer implements Authorizer {

    @Override
    public boolean authorize(Principal principal, String validRole) {
        return false;
    }
}
