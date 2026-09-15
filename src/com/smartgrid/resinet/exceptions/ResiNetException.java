package com.smartgrid.resinet.exceptions;

/**
 * Base checked exception for the ResiNet Smart Microgrid platform.
 */
public class ResiNetException extends Exception {
    private final String errorCode;

    public ResiNetException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResiNetException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
