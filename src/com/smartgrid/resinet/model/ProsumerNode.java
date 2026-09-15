package com.smartgrid.resinet.model;

import com.smartgrid.resinet.exceptions.InsufficientEnergyException;
import com.smartgrid.resinet.interfaces.MonitoredDevice;
import com.smartgrid.resinet.interfaces.Tradeable;

/**
 * Node representing an energy producer with localized renewable generation (e.g. Solar rooftop, Wind turbine).
 */
public class ProsumerNode extends EnergyNode implements Tradeable, MonitoredDevice {
    private String generationType; // "SOLAR", "WIND", "HYDRO"
    private double peakCapacityKW;
    private double sellPricePerKWh;
    private double efficiencyRating; // 0.85 = 85%

    public ProsumerNode(String nodeId, String name, String locationZone, double initialPowerKW,
                        String generationType, double peakCapacityKW, double sellPricePerKWh) {
        super(nodeId, name, locationZone, initialPowerKW);
        this.generationType = generationType;
        this.peakCapacityKW = peakCapacityKW;
        this.sellPricePerKWh = sellPricePerKWh;
        this.efficiencyRating = 0.95;
    }

    public String getGenerationType() {
        return generationType;
    }

    public double getPeakCapacityKW() {
        return peakCapacityKW;
    }

    @Override
    public String getNodeType() {
        return "PROSUMER";
    }

    @Override
    public double calculateNetEnergyFlow() {
        return currentPowerKW * efficiencyRating;
    }

    // Tradeable Implementation
    @Override
    public double getAvailablePowerKW() {
        return currentPowerKW;
    }

    @Override
    public double getBuyPricePerKWh() {
        return 0.0; // Prosumers primarily sell
    }

    @Override
    public double getSellPricePerKWh() {
        return sellPricePerKWh;
    }

    @Override
    public synchronized void deductPower(double amountKW) throws InsufficientEnergyException {
        if (amountKW > currentPowerKW) {
            throw new InsufficientEnergyException(
                    "Prosumer " + getNodeId() + " cannot sell " + amountKW + " kW. Available: " + currentPowerKW + " kW",
                    amountKW, currentPowerKW);
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
        return String.format("Prosumer [%s] %s: Yielding %.2f kW (%s)",
                getNodeId(), getName(), currentPowerKW, generationType);
    }

    @Override
    public double getHealthIndex() {
        return efficiencyRating;
    }

    @Override
    public boolean isOperational() {
        return isActive() && currentPowerKW >= 0;
    }

    @Override
    public void performSelfDiagnostic() {
        if (currentPowerKW > peakCapacityKW) {
            this.efficiencyRating = 0.88;
        } else {
            this.efficiencyRating = 0.96;
        }
    }
}
