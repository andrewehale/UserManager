/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrewhale.usermanager.api;

import com.andrewhale.usermanager.Err;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * @author andrew
 */
public class Status {

    private int errorCode;
    private String errorMessage;

    public Status() {
        this.errorCode = 0;
        this.errorMessage = "No message.";
    }

    public Status(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.errorCode;
        hash = 79 * hash + Objects.hashCode(this.errorMessage);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Status other = (Status) obj;
        if (this.errorCode != other.errorCode) {
            return false;
        }
        if (!Objects.equals(this.errorMessage, other.errorMessage)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Status{" + "errorCode=" + errorCode + ", errorMessage=" + errorMessage + '}';
    }

    @JsonIgnore
    public boolean isGood() {
        return (this.errorCode == Err.NONE);
    }
}
