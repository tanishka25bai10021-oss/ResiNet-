package com.smartgrid.resinet.engine;

import com.smartgrid.resinet.db.EnergyNodeDAO;
import com.smartgrid.resinet.exceptions.GridOverloadException;
import com.smartgrid.resinet.exceptions.InsufficientEnergyException;
import com.smartgrid.resinet.model.BatteryStorageNode;
import com.smartgrid.resinet.model.ConsumerNode;
import com.smartgrid.resinet.model.EnergyNode;
import com.smartgrid.resinet.model.ProsumerNode;
import com.smartgrid.resinet.model.TradeOrder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * GridSimulationEngine - Core multithreaded simulation controller.
 * Demonstrates Synchronization, ReentrantLocks, Thread Pools/Loops, and Exception Handling.
 * Fulfills CSE2006 Unit 3 (Multithreading & Exception Handling).
 */
public class GridSimulationEngine implements Runnable {
    private final Map<String, EnergyNode> nodeRegistry;
    private final OrderMatchingEngine matchingEngine;
    private final TelemetryLoggerThread loggerThread;
    private final EnergyNodeDAO nodeDAO;
    private final ReentrantLock gridLock;
    private final double maxTransformerCapacityKW;
    private volatile boolean running;
    private final Random random;

    public GridSimulationEngine(OrderMatchingEngine matchingEngine,
                                TelemetryLoggerThread loggerThread,
                                double maxTransformerCapacityKW) {
        this.nodeRegistry = new ConcurrentHashMap<>();
        this.matchingEngine = matchingEngine;
        this.loggerThread = loggerThread;
        this.nodeDAO = new EnergyNodeDAO();
        this.gridLock = new ReentrantLock();
        this.maxTransformerCapacityKW = maxTransformerCapacityKW;
        this.running = false;
        this.random = new Random();
    }

    public void registerNode(EnergyNode node) {
        gridLock.lock();
        try {
            nodeRegistry.put(node.getNodeId(), node);
            try {
                nodeDAO.insertNode(node);
            } catch (SQLException e) {
                System.err.println("[GridEngine] Error saving node to DB: " + e.getMessage());
            }
            loggerThread.logEvent("Registered new node: " + node.toString());
        } finally {
            gridLock.unlock();
        }
    }

    public double calculateTotalGridDemandKW() {
        return nodeRegistry.values().stream()
                .filter(n -> n instanceof ConsumerNode)
                .mapToDouble(n -> ((ConsumerNode) n).getDemandDemandKW())
                .sum();
    }

    public double calculateTotalGridGenerationKW() {
        return nodeRegistry.values().stream()
                .filter(n -> n instanceof ProsumerNode)
                .mapToDouble(EnergyNode::getCurrentPowerKW)
                .sum();
    }

    public void checkGridSafetyLimits() throws GridOverloadException {
        double currentDemand = calculateTotalGridDemandKW();
        if (currentDemand > maxTransformerCapacityKW) {
            throw new GridOverloadException(
                    "WARNING: Grid demand (" + currentDemand + " kW) exceeds safe transformer limit (" + maxTransformerCapacityKW + " kW)!",
                    currentDemand, maxTransformerCapacityKW);
        }
    }

    @Override
    public void run() {
        this.running = true;
        loggerThread.logEvent("Grid Simulation Engine started.");

        int cycle = 0;
        while (running) {
            cycle++;
            gridLock.lock();
            try {
                // 1. Simulate solar/wind fluctuation for prosumers
                for (EnergyNode node : nodeRegistry.values()) {
                    if (node instanceof ProsumerNode) {
                        ProsumerNode prosumer = (ProsumerNode) node;
                        double solarFactor = 0.7 + (random.nextDouble() * 0.5); // 70% - 120%
                        double newPower = Math.round(prosumer.getPeakCapacityKW() * solarFactor * 100.0) / 100.0;
                        prosumer.addPower(newPower - prosumer.getCurrentPowerKW());

                        // Automatically submit sell offer
                        TradeOrder sellOrder = new TradeOrder(
                                "ORD-SELL-" + cycle + "-" + prosumer.getNodeId(),
                                prosumer.getNodeId(),
                                TradeOrder.OrderType.SELL,
                                prosumer.getAvailablePowerKW(),
                                prosumer.getSellPricePerKWh()
                        );
                        matchingEngine.submitOrder(sellOrder);
                    } else if (node instanceof ConsumerNode) {
                        ConsumerNode consumer = (ConsumerNode) node;
                        double demandFactor = 0.8 + (random.nextDouble() * 0.4);
                        double currentDemand = Math.round(consumer.getDemandDemandKW() * demandFactor * 100.0) / 100.0;

                        TradeOrder buyOrder = new TradeOrder(
                                "ORD-BUY-" + cycle + "-" + consumer.getNodeId(),
                                consumer.getNodeId(),
                                TradeOrder.OrderType.BUY,
                                currentDemand,
                                consumer.getBuyPricePerKWh()
                        );
                        matchingEngine.submitOrder(buyOrder);
                    }
                }

                // 2. Safety limit check
                checkGridSafetyLimits();

            } catch (GridOverloadException e) {
                loggerThread.logEvent("[CRITICAL ALARM] " + e.getMessage());
                System.err.println("\n[GRID ALARM] " + e.getMessage());
            } catch (Exception e) {
                loggerThread.logEvent("[ERROR] Simulation loop error: " + e.getMessage());
            } finally {
                gridLock.unlock();
            }

            try {
                Thread.sleep(3000); // 3-second simulation step
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        loggerThread.logEvent("Grid Simulation Engine stopped.");
    }

    public void stopSimulation() {
        this.running = false;
    }

    public List<EnergyNode> getAllNodes() {
        return new ArrayList<>(nodeRegistry.values());
    }
}
