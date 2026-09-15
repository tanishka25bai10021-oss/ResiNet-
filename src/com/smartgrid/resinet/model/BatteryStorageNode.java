package com.smartgrid.resinet.model;

import com.smartgrid.resinet.exceptions.InsufficientEnergyException;
import com.smartgrid.resinet.interfaces.MonitoredDevice;
import com.smartgrid.resinet.interfaces.Tradeable;

/**
 * Node representing a smart battery energy storage system (BESS).
 * Functions as both buyer (when charging) and seller (when discharging).
 */
public class BatteryStorageNode extends EnergyNode implements Tradeable, MonitoredDevice {
    private double maxCapacityKWh;
    private double currentStoredKWh;
    private double maxDischargeRateKW;
    private double buyPricePerKWh;
    private double sellPricePerKWh;

    public BatteryStorageNode(String nodeId, String name, String locationZone,
                              double maxCapacityKWh, double initialStoredKWh,
                              double maxDischargeRateKW, double buyPricePerKWh, double sellPricePerKWh) {
        super(nodeId, name, locationZone, initialStoredKWh);
        this.maxCapacityKWh = maxCapacityKWh;
        this.currentStoredKWh = Math.min(initialStoredKWh, maxCapacityKWh);
        this.maxDischargeRateKW = maxDischargeRateKW;
        this.buyPricePerKWh = buyPricePerKWh;
        this.sellPricePerKWh = sellPricePerKWh;
    }

    public double getSOCPercentage() {
        return (currentStoredKWh / maxCapacityKWh) * 100.0;
    }

    public double getMaxCapacityKWh() {
        return maxCapacityKWh;
    }

    public double getCurrentStoredKWh() {
        return currentStoredKWh;
    }

    @Override
    public String getNodeType() {
        return "BATTERY_STORAGE";
    }

    @Override
    public double calculateNetEnergyFlow() {
        return currentStoredKWh;
    }

    // Tradeable Implementation
    @Override
    public double getAvailablePowerKW() {
        return Math.min(currentStoredKWh, maxDischargeRateKW);
    }

    @Override
    public double getBuyPricePerKWh() {
        return buyPricePerKWh;
    }

    @Override
    public double getSellPricePerKWh() {
        return sellPricePerKWh;
    }

    @Override
    public synchronized void deductPower(double amountKW) throws InsufficientEnergyException {
        if (amountKW > currentStoredKWh) {
            throw new InsufficientEnergyException(
                    "Battery Storage " + getNodeId() + " has insufficient charge (" + currentStoredKWh + " kWh) for request (" + amountKW + " kW)",
                    amountKW, currentStoredKWh);
        }
        currentStoredKWh -= amountKW;
        currentPowerKW = currentStoredKWh;
    }

    @Override
    public synchronized void addPower(double amountKW) {
        currentStoredKWh = Math.min(maxCapacityKWh, currentStoredKWh + amountKW);
        currentPowerKW = currentStoredKWh;
    }

    // MonitoredDevice Implementation
    @Override
    public String getStatusReport() {
        return String.format("BESS [%s] %s: SOC %.1f%% (%.2f / %.2f kWh)",
                getNodeId(), getName(), getSOCPercentage(), currentStoredKWh, maxCapacityKWh);
    }

    @Override
    public double getHealthIndex() {
        return 0.98; // Simulated health metric
    }

    @Override
    public boolean isOperational() {
        return isActive() && currentStoredKWh > 0;
    }

    @Override
    public void performSelfDiagnostic() {
        // Battery cell diagnostics
    }
}
