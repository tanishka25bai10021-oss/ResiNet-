package com.smartgrid.resinet.model;

import java.time.LocalDateTime;

/**
 * Record of a executed energy trade between a buyer and seller node.
 */
public class EnergyTransaction {
    private final String transactionId;
    private final String sellerNodeId;
    private final String buyerNodeId;
    private final double matchedPowerKW;
    private final double pricePerKWh;
    private final double totalCost;
    private final LocalDateTime timestamp;

    public EnergyTransaction(String transactionId, String sellerNodeId, String buyerNodeId,
                             double matchedPowerKW, double pricePerKWh) {
        this.transactionId = transactionId;
        this.sellerNodeId = sellerNodeId;
        this.buyerNodeId = buyerNodeId;
        this.matchedPowerKW = matchedPowerKW;
        this.pricePerKWh = pricePerKWh;
        this.totalCost = matchedPowerKW * pricePerKWh;
        this.timestamp = LocalDateTime.now();
    }

    public EnergyTransaction(String transactionId, String sellerNodeId, String buyerNodeId,
                             double matchedPowerKW, double pricePerKWh, double totalCost, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.sellerNodeId = sellerNodeId;
        this.buyerNodeId = buyerNodeId;
        this.matchedPowerKW = matchedPowerKW;
        this.pricePerKWh = pricePerKWh;
        this.totalCost = totalCost;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public String getSellerNodeId() { return sellerNodeId; }
    public String getBuyerNodeId() { return buyerNodeId; }
    public double getMatchedPowerKW() { return matchedPowerKW; }
    public double getPricePerKWh() { return pricePerKWh; }
    public double getTotalCost() { return totalCost; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Tx[%s]: Seller %s -> Buyer %s | %.2f kW @ $%.3f/kWh | Total: $%.2f",
                transactionId, sellerNodeId, buyerNodeId, matchedPowerKW, pricePerKWh, totalCost);
    }
}
