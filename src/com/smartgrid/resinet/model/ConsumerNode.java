package com.smartgrid.resinet.model;

import com.smartgrid.resinet.exceptions.InsufficientEnergyException;
import com.smartgrid.resinet.interfaces.MonitoredDevice;
import com.smartgrid.resinet.interfaces.Tradeable;

/**
 * Node representing an energy consumer (e.g. Residential home, Commercial building, EV Charging station).
 */
public class ConsumerNode extends EnergyNode implements Tradeable, MonitoredDevice {
    private String category; // "RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL"
    private double demandDemandKW;
    private double maxBuyPricePerKWh;

    public ConsumerNode(String nodeId, String name, String locationZone, double demandDemandKW,
                        String category, double maxBuyPricePerKWh) {
        super(nodeId, name, locationZone, 0.0);
        this.demandDemandKW = demandDemandKW;
        this.category = category;
        this.maxBuyPricePerKWh = maxBuyPricePerKWh;
    }

    public String getCategory() {
        return category;
    }

    public double getDemandDemandKW() {
        return demandDemandKW;
    }

    public void setDemandDemandKW(double demandDemandKW) {
        this.demandDemandKW = demandDemandKW;
    }

    @Override
    public String getNodeType() {
        return "CONSUMER";
    }

    @Override
    public double calculateNetEnergyFlow() {
        return -demandDemandKW; // Negative net flow indicates energy consumption
    }

    // Tradeable Implementation
    @Override
    public double getAvailablePowerKW() {
        return currentPowerKW; // Received power
    }

    @Override
    public double getBuyPricePerKWh() {
        return maxBuyPricePerKWh;
    }

    @Override
    public double getSellPricePerKWh() {
        return 0.0;
    }

    @Override
    public synchronized void deductPower(double amountKW) throws InsufficientEnergyException {
        if (amountKW > currentPowerKW) {
            throw new InsufficientEnergyException(
                    "Consumer " + getNodeId() + " cannot deduct " + amountKW + " kW.", amountKW, currentPowerKW);
        }
        currentPowerKW -= amountKW;
    }

    @Override
    public synchronized void addPower(double amountKW) {
        currentPowerKW += amountKW;
    }

    // MonitoredDevice Implementation
    @Override
    public String getStatusReport() {
        return String.format("Consumer [%s] %s: Demand %.2f kW | Fulfilled %.2f kW",
                getNodeId(), getName(), demandDemandKW, currentPowerKW);
    }

    @Override
    public double getHealthIndex() {
        return (demandDemandKW > 0) ? Math.min(1.0, currentPowerKW / demandDemandKW) : 1.0;
    }

    @Override
    public boolean isOperational() {
        return isActive();
    }

    @Override
    public void performSelfDiagnostic() {
        // Diagnostic check
    }
}
