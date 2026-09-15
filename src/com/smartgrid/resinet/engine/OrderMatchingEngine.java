package com.smartgrid.resinet.engine;

import com.smartgrid.resinet.db.TransactionDAO;
import com.smartgrid.resinet.exceptions.InvalidTradeOrderException;
import com.smartgrid.resinet.model.EnergyTransaction;
import com.smartgrid.resinet.model.TradeOrder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.PriorityQueue;
import java.util.UUID;

/**
 * OrderMatchingEngine - PriorityQueue-based order matching system.
 * Matches highest BUY bid prices against lowest SELL ask prices concurrently.
 */
public class OrderMatchingEngine {
    private final PriorityQueue<TradeOrder> buyOrders;
    private final PriorityQueue<TradeOrder> sellOrders;
    private final TransactionDAO transactionDAO;

    public OrderMatchingEngine(TransactionDAO transactionDAO) {
        this.buyOrders = new PriorityQueue<>();
        this.sellOrders = new PriorityQueue<>();
        this.transactionDAO = transactionDAO;
    }

    public synchronized List<EnergyTransaction> submitOrder(TradeOrder order) throws InvalidTradeOrderException {
        if (order.getPowerAmountKW() <= 0) {
            throw new InvalidTradeOrderException("Order power amount must be greater than zero.");
        }
        if (order.getPricePerKWh() <= 0) {
            throw new InvalidTradeOrderException("Order price per kWh must be greater than zero.");
        }

        if (order.getOrderType() == TradeOrder.OrderType.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        return processMatches();
    }

    public synchronized List<EnergyTransaction> processMatches() {
        List<EnergyTransaction> matchedTransactions = new ArrayList<>();

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            TradeOrder topBuy = buyOrders.peek();
            TradeOrder topSell = sellOrders.peek();

            // Match condition: Buyer bid price >= Seller ask price
            if (topBuy.getPricePerKWh() >= topSell.getPricePerKWh()) {
                double tradeVolumeKW = Math.min(topBuy.getPowerAmountKW(), topSell.getPowerAmountKW());
                double executionPrice = (topBuy.getPricePerKWh() + topSell.getPricePerKWh()) / 2.0;

                String txId = "TX-" + UUID.randomUUID().toString().substring(0, 8);
                EnergyTransaction tx = new EnergyTransaction(
                        txId, topSell.getNodeId(), topBuy.getNodeId(), tradeVolumeKW, executionPrice);

                matchedTransactions.add(tx);
                System.out.printf("[OrderMatcher] MATCHED: Seller %s -> Buyer %s (%.2f kW @ $%.3f/kWh)%n",
                        topSell.getNodeId(), topBuy.getNodeId(), tradeVolumeKW, executionPrice);

                if (transactionDAO != null) {
                    try {
                        transactionDAO.insertTransaction(tx);
                    } catch (SQLException e) {
                        System.err.println("[OrderMatcher] DB Log error: " + e.getMessage());
                    }
                }

                // Update order amounts
                topBuy.setPowerAmountKW(topBuy.getPowerAmountKW() - tradeVolumeKW);
                topSell.setPowerAmountKW(topSell.getPowerAmountKW() - tradeVolumeKW);

                if (topBuy.getPowerAmountKW() <= 0.0001) {
                    buyOrders.poll();
                }
                if (topSell.getPowerAmountKW() <= 0.0001) {
                    sellOrders.poll();
                }
            } else {
                break; // Highest buy price is lower than lowest sell price
            }
        }
        return matchedTransactions;
    }

    public synchronized int getPendingBuyCount() { return buyOrders.size(); }
    public synchronized int getPendingSellCount() { return sellOrders.size(); }
}
