package com.andrewhale.usermanager.auth;

import com.andrewhale.usermanager.api.User;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

/**
 * Created by andrew on 7/1/2016.
 */
public class UserManagerAuthenticator implements Authenticator<JwtContext, User> {


    @Override
    public Optional<User> authenticate(JwtContext jwtContext) throws AuthenticationException {
        try {
            String subject = jwtContext.getJwtClaims().getSubject();

            // TODO probably need to send email address as subject when create token
            // call to generate-valid needs to check password
            // I guess authenticate will create user object, load permissions and stuff?
            // and then authorize will check roles

            if ("good-guy".equals(subject)) {
                return Optional.of(new User(0, "test", "test"));
            }
            return Optional.absent();
        }
        catch (MalformedClaimException e)
        {
            return Optional.absent();
        }
    }
}
