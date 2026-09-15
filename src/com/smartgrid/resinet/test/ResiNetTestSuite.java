package com.smartgrid.resinet.test;

import com.smartgrid.resinet.db.DatabaseManager;
import com.smartgrid.resinet.db.EnergyNodeDAO;
import com.smartgrid.resinet.db.TransactionDAO;
import com.smartgrid.resinet.engine.OrderMatchingEngine;

import com.smartgrid.resinet.exceptions.GridOverloadException;
import com.smartgrid.resinet.exceptions.InsufficientEnergyException;
import com.smartgrid.resinet.exceptions.InvalidTradeOrderException;
import com.smartgrid.resinet.model.ConsumerNode;
import com.smartgrid.resinet.model.EnergyTransaction;

import com.smartgrid.resinet.model.ProsumerNode;
import com.smartgrid.resinet.model.TradeOrder;

import java.sql.SQLException;
import java.util.List;

/**
 * ResiNetTestSuite - Automated Unit Test runner validating OOP, Concurrency, Exceptions, and JDBC persistence.
 */
public class ResiNetTestSuite {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("           RUNNING RESINET SYSTEM UNIT TEST SUITE               ");
        System.out.println("=================================================================\n");

        testOOPPolymorphismAndEncapsulation();
        testInsufficientEnergyException();
        testOrderMatchingEnginePriorityQueue();
        testInvalidTradeOrderException();
        testDatabaseManagerAndDAO();

        System.out.println("\n-----------------------------------------------------------------");
        System.out.printf("TEST SUITE RESULTS: %d PASSED | %d FAILED | TOTAL: %d%n",
                passedTests, failedTests, (passedTests + failedTests));
        System.out.println("-----------------------------------------------------------------");

        if (failedTests > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            passedTests++;
        } else {
            System.err.println("[FAIL] " + testName);
            failedTests++;
        }
    }

    private static void testOOPPolymorphismAndEncapsulation() {
        System.out.println("--- Test Category: OOP & Polymorphism ---");
        ProsumerNode prosumer = new ProsumerNode("P-TEST-01", "Solar Park", "Zone-A", 150.0, "SOLAR", 200.0, 0.12);
        ConsumerNode consumer = new ConsumerNode("C-TEST-01", "Tech Campus", "Zone-A", 80.0, "COMMERCIAL", 0.15);

        assertTrue("PROSUMER".equals(prosumer.getNodeType()), "Prosumer polymorphic type check");
        assertTrue("CONSUMER".equals(consumer.getNodeType()), "Consumer polymorphic type check");
        assertTrue(prosumer.getAvailablePowerKW() == 150.0, "Encapsulation power getter check");
    }

    private static void testInsufficientEnergyException() {
        System.out.println("\n--- Test Category: Exception Handling ---");
        ProsumerNode prosumer = new ProsumerNode("P-TEST-02", "Wind Farm", "Zone-B", 30.0, "WIND", 100.0, 0.10);

        boolean exceptionThrown = false;
        try {
            prosumer.deductPower(50.0); // Demanding 50 kW when only 30 kW available
        } catch (InsufficientEnergyException e) {
            exceptionThrown = true;
            assertTrue("ERR_INSUFFICIENT_ENERGY".equals(e.getErrorCode()), "Exception error code match");
        }
        assertTrue(exceptionThrown, "InsufficientEnergyException thrown correctly on power deficit");
    }

    private static void testOrderMatchingEnginePriorityQueue() {
        System.out.println("\n--- Test Category: PriorityQueue & Order Matching ---");
        TransactionDAO txDAO = new TransactionDAO();
        OrderMatchingEngine matcher = new OrderMatchingEngine(txDAO);

        try {
            TradeOrder sellOrder = new TradeOrder("ORD-S1", "P-TEST-01", TradeOrder.OrderType.SELL, 50.0, 0.10);
            TradeOrder buyOrder = new TradeOrder("ORD-B1", "C-TEST-01", TradeOrder.OrderType.BUY, 50.0, 0.14);

            matcher.submitOrder(sellOrder);
            List<EnergyTransaction> matches = matcher.submitOrder(buyOrder);

            assertTrue(matches.size() >= 1, "Order matching engine successfully paired buy/sell orders");
        } catch (Exception e) {
            assertTrue(false, "Order matching threw unexpected exception: " + e.getMessage());
        }
    }

    private static void testInvalidTradeOrderException() {
        OrderMatchingEngine matcher = new OrderMatchingEngine(new TransactionDAO());
        boolean exceptionThrown = false;
        try {
            TradeOrder invalidOrder = new TradeOrder("ORD-BAD", "P-TEST-01", TradeOrder.OrderType.SELL, -10.0, 0.10);
            matcher.submitOrder(invalidOrder);
        } catch (InvalidTradeOrderException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "InvalidTradeOrderException thrown on negative power order");
    }

    private static void testDatabaseManagerAndDAO() {
        System.out.println("\n--- Test Category: JDBC Database & DAO ---");
        EnergyNodeDAO nodeDAO = new EnergyNodeDAO();
        ProsumerNode testNode = new ProsumerNode("DB-NODE-01", "DB Solar", "Zone-C", 100.0, "SOLAR", 150.0, 0.11);

        try {
            boolean inserted = nodeDAO.insertNode(testNode);
            assertTrue(inserted, "JDBC PreparedStatement inserted node into SQLite successfully");

            int activeCount = nodeDAO.getActiveNodeCount();
            assertTrue(activeCount > 0, "JDBC SELECT count retrieved active nodes from SQLite");
        } catch (SQLException e) {
            assertTrue(false, "JDBC DAO operation failed: " + e.getMessage());
        }
    }
}
