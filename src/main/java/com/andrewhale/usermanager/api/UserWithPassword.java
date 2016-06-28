package com.andrewhale.usermanager.api;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by andrew on 6/21/2016.
 */
public class UserWithPassword {
    private String emailAddress;
    private String password;
    private long userId;
    private byte[] salt;

    public UserWithPassword() {
        this.userId = 0L;
        this.emailAddress = null;
        this.password = null;
    }

    public UserWithPassword(long userId, String emailAddress, String password) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        setPassword(password);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
    public void setPassword(String password) {
        this.password = password;
        // Generate the salt
        SecureRandom random = new SecureRandom();
        salt = new byte[256];
        random.nextBytes(salt);
    }

    public byte[] getSalt() {
        return salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWithPassword that = (UserWithPassword) o;

        if (userId != that.userId) return false;
        if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return Arrays.equals(salt, that.salt);

    }

    @Override
    public int hashCode() {
        int result = emailAddress != null ? emailAddress.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + Arrays.hashCode(salt);
        return result;
    }

    @Override
    public String toString() {
        return "UserWithPassword{" +
                "emailAddress='" + emailAddress + '\'' +
                ", password=*" +
                ", userId=" + userId +
                ", salt=" + Arrays.toString(salt) +
                '}';
    }
}
