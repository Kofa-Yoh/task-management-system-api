package com.kotkina.taskManagementSystemApi.exceptions;

public class NoRightsForEntityChangeException extends RuntimeException {

    public NoRightsForEntityChangeException(String message) {
        super(message);
    }
}
