package com.andrewhale.usermanager.api;

/**
 * Created by andrew on 6/21/2016.
 */
public class NewUserToken {
    private String emailAddress;
    private String password;
    private String name;

    public NewUserToken() {
        this.emailAddress = null;
        this.password = null;
        this.name = null;
    }

    public NewUserToken(String emailAddress, String password, String name) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewUserToken that = (NewUserToken) o;

        if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = emailAddress != null ? emailAddress.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NewUserToken{" +
                "emailAddress='" + emailAddress + '\'' +
                ", password=*'" +
                ", name='" + name + '\'' +
                '}';
    }
}
