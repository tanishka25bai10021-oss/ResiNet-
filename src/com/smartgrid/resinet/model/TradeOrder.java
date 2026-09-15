package com.smartgrid.resinet.model;

import java.time.LocalDateTime;

/**
 * Represents a buy or sell trade order submitted to the ResiNet P2P matching engine.
 */
public class TradeOrder implements Comparable<TradeOrder> {
    public enum OrderType { BUY, SELL }

    private final String orderId;
    private final String nodeId;
    private final OrderType orderType;
    private double powerAmountKW;
    private final double pricePerKWh;
    private final LocalDateTime timestamp;

    public TradeOrder(String orderId, String nodeId, OrderType orderType, double powerAmountKW, double pricePerKWh) {
        this.orderId = orderId;
        this.nodeId = nodeId;
        this.orderType = orderType;
        this.powerAmountKW = powerAmountKW;
        this.pricePerKWh = pricePerKWh;
        this.timestamp = LocalDateTime.now();
    }

    public String getOrderId() { return orderId; }
    public String getNodeId() { return nodeId; }
    public OrderType getOrderType() { return orderType; }
    public double getPowerAmountKW() { return powerAmountKW; }
    public void setPowerAmountKW(double powerAmountKW) { this.powerAmountKW = powerAmountKW; }
    public double getPricePerKWh() { return pricePerKWh; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public int compareTo(TradeOrder other) {
        if (this.orderType == OrderType.BUY) {
            // Buyers prefer HIGHEST bid price first
            int priceCompare = Double.compare(other.pricePerKWh, this.pricePerKWh);
            return (priceCompare != 0) ? priceCompare : this.timestamp.compareTo(other.timestamp);
        } else {
            // Sellers prefer LOWEST ask price first
            int priceCompare = Double.compare(this.pricePerKWh, other.pricePerKWh);
            return (priceCompare != 0) ? priceCompare : this.timestamp.compareTo(other.timestamp);
        }
    }

    @Override
    public String toString() {
        return String.format("Order[%s | Node:%s | %s | %.2f kW @ $%.3f/kWh]",
                orderId, nodeId, orderType, powerAmountKW, pricePerKWh);
    }
}
