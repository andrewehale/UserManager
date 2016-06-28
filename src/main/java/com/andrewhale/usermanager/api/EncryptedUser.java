package com.andrewhale.usermanager.api;

import java.util.Arrays;

/**
 * Created by andrew on 6/21/2016.
 */
public class EncryptedUser {
    private String emailAddress;
    private byte[] password;
    private long userId;
    private byte[] salt;

    public EncryptedUser() {
        this.userId = 0L;
        this.emailAddress = null;
        this.password = null;
    }

    public EncryptedUser(long userId, String emailAddress, byte[] password, byte[] salt) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.password = password;
        this.salt = salt;
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

    public byte[] getPassword() {
        return password;
    }

    /**
     * This function generates a random salt for the user, then encrypts the password. Perhaps not the best
     * place to do this but it minimizes the time the password is stored in plain text.
     *
     * @param password Unencrpyted password.
     */
    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncryptedUser that = (EncryptedUser) o;

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
