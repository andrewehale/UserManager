package com.andrewhale.usermanager.api;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by andrew on 6/21/2016.
 */
public class AuthToken {
    private String emailAddress;
    private String password;

    public AuthToken() {
        this.emailAddress = null;
        this.password = null;
    }

    public AuthToken(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    /**
     * This function generates a random salt for the user, then encrypts the password. Perhaps not the best
     * place to do this but it minimizes the time the password is stored in plain text.
     *
     * @param password Unencrpyted password.
     */
    public void xxsetPassword(String password) {
        this.password = password;
        // Generate the salt

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthToken authToken = (AuthToken) o;

        if (emailAddress != null ? !emailAddress.equals(authToken.emailAddress) : authToken.emailAddress != null)
            return false;
        return password != null ? password.equals(authToken.password) : authToken.password == null;

    }

    @Override
    public int hashCode() {
        int result = emailAddress != null ? emailAddress.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "emailAddress='" + emailAddress + '\'' +
                ", password=*'" +
                '}';
    }
}
