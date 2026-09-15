package com.smartgrid.resinet.interfaces;

import com.smartgrid.resinet.exceptions.InsufficientEnergyException;

/**
 * Contract for grid nodes capable of buying or selling energy.
 */
public interface Tradeable {
    String getNodeId();
    double getAvailablePowerKW();
    double getBuyPricePerKWh();
    double getSellPricePerKWh();
    
    void deductPower(double amountKW) throws InsufficientEnergyException;
    void addPower(double amountKW);
}
