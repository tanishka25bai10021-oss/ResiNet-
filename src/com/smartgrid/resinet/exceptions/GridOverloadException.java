package com.smartgrid.resinet.exceptions;

/**
 * Exception thrown when total grid load exceeds maximum safe transformer capacity.
 */
public class GridOverloadException extends ResiNetException {
    private final double currentLoadKW;
    private final double maxCapacityKW;

    public GridOverloadException(String message, double currentLoadKW, double maxCapacityKW) {
        super(message, "ERR_GRID_OVERLOAD");
        this.currentLoadKW = currentLoadKW;
        this.maxCapacityKW = maxCapacityKW;
    }

    public double getCurrentLoadKW() {
        return currentLoadKW;
    }

    public double getMaxCapacityKW() {
        return maxCapacityKW;
    }
}
