package com.smartgrid.resinet.model;

import com.smartgrid.resinet.exceptions.InsufficientEnergyException;

/**
 * Abstract Base class for all physical microgrid entities.
 * Demonstrates Java Object-Oriented Principles: Encapsulation, Abstraction, and Polymorphism.
 */
public abstract class EnergyNode {
    private final String nodeId;
    private String name;
    private String locationZone;
    protected double currentPowerKW;
    private boolean active;

    public EnergyNode(String nodeId, String name, String locationZone, double initialPowerKW) {
        this.nodeId = nodeId;
        this.name = name;
        this.locationZone = locationZone;
        this.currentPowerKW = Math.max(0, initialPowerKW);
        this.active = true;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationZone() {
        return locationZone;
    }

    public void setLocationZone(String locationZone) {
        this.locationZone = locationZone;
    }

    public double getCurrentPowerKW() {
        return currentPowerKW;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Abstract methods to be customized by subclasses
    public abstract String getNodeType();
    public abstract double calculateNetEnergyFlow();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Power: %.2f kW - Active: %b",
                getNodeType(), name, nodeId, currentPowerKW, active);
    }
}
