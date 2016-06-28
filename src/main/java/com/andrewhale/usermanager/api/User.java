package com.andrewhale.usermanager.api;

/**
 * Created by andrew on 6/21/2016.
 */
public class User {
    private String emailAddress;
    private long userId;

    public User() {
        this.userId = 0L;
        this.emailAddress = null;
    }

    public User(long userId, String emailAddress) {
        this.userId = userId;
        this.emailAddress = emailAddress;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != user.userId) return false;
        return emailAddress != null ? emailAddress.equals(user.emailAddress) : user.emailAddress == null;

    }

    @Override
    public int hashCode() {
        int result = emailAddress != null ? emailAddress.hashCode() : 0;
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "emailAddress='" + emailAddress + '\'' +
                ", userId=" + userId +
                '}';
    }
}
