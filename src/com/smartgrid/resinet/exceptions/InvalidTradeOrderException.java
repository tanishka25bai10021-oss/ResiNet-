package com.smartgrid.resinet.exceptions;

/**
 * Exception thrown when a trade order fails validation criteria (e.g. non-positive power, price mismatch).
 */
public class InvalidTradeOrderException extends ResiNetException {
    public InvalidTradeOrderException(String message) {
        super(message, "ERR_INVALID_ORDER");
    }
}
