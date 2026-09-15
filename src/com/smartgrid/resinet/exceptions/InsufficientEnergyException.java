package com.smartgrid.resinet.exceptions;

/**
 * Exception thrown when a node or battery attempts to dispatch more power than available.
 */
public class InsufficientEnergyException extends ResiNetException {
    private final double requestedKW;
    private final double availableKW;

    public InsufficientEnergyException(String message, double requestedKW, double availableKW) {
        super(message, "ERR_INSUFFICIENT_ENERGY");
        this.requestedKW = requestedKW;
        this.availableKW = availableKW;
    }

    public double getRequestedKW() {
        return requestedKW;
    }

    public double getAvailableKW() {
        return availableKW;
    }
}
