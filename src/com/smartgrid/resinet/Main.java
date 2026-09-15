package com.smartgrid.resinet;

import com.smartgrid.resinet.db.EnergyNodeDAO;
import com.smartgrid.resinet.db.TransactionDAO;
import com.smartgrid.resinet.engine.GridSimulationEngine;
import com.smartgrid.resinet.engine.OrderMatchingEngine;
import com.smartgrid.resinet.engine.TelemetryLoggerThread;

import com.smartgrid.resinet.model.BatteryStorageNode;
import com.smartgrid.resinet.model.ConsumerNode;
import com.smartgrid.resinet.model.EnergyNode;
import com.smartgrid.resinet.model.EnergyTransaction;
import com.smartgrid.resinet.model.ProsumerNode;
import com.smartgrid.resinet.model.TradeOrder;

import com.smartgrid.resinet.test.ResiNetTestSuite;
import com.smartgrid.resinet.util.ReportGenerator;

import java.util.List;
import java.util.Scanner;

/**
 * ResiNet Main Entry Point - Interactive CLI Dashboard & Evaluation Demonstration Application.
 */
public class Main {
    // ANSI Formatting Colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BOLD = "\u001B[1m";

    private static GridSimulationEngine simulationEngine;
    private static OrderMatchingEngine matchingEngine;
    private static TelemetryLoggerThread loggerThread;
    private static TransactionDAO transactionDAO;
    private static EnergyNodeDAO nodeDAO;
    private static Thread simThread;

    public static void main(String[] args) {
        System.out.println(ANSI_BOLD + ANSI_CYAN + "=========================================================================" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_GREEN + "    RESINET: MULTI-THREADED SMART MICROGRID ENERGY DISPATCH ENGINE      " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "    Course Code: CSE2006 (Programming in Java) Project Submission        " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "=========================================================================\n" + ANSI_RESET);

        // Initialize Services
        loggerThread = new TelemetryLoggerThread("logs/grid_telemetry.log");
        loggerThread.start();

        transactionDAO = new TransactionDAO();
        nodeDAO = new EnergyNodeDAO();
        matchingEngine = new OrderMatchingEngine(transactionDAO);
        simulationEngine = new GridSimulationEngine(matchingEngine, loggerThread, 1500.0); // 1500 kW max capacity

        // Pre-populate Default Nodes
        initDefaultNodes();

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            printMenu();
            System.out.print(ANSI_BOLD + "Select Option [1-8]: " + ANSI_RESET);
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    viewLiveDashboard();
                    break;
                case "2":
                    submitCustomOrder(scanner);
                    break;
                case "3":
                    toggleSimulation();
                    break;
                case "4":
                    viewDatabaseTransactions();
                    break;
                case "5":
                    addNewNode(scanner);
                    break;
                case "6":
                    exportAuditReport();
                    break;
                case "7":
                    runSystemTests();
                    break;
                case "8":
                    exit = true;
                    shutdown();
                    System.out.println(ANSI_GREEN + "Thank you for evaluating ResiNet Smart Microgrid Platform!" + ANSI_RESET);
                    break;
                default:
                    System.out.println(ANSI_RED + "Invalid choice! Please select 1 through 8." + ANSI_RESET);
            }
            System.out.println("\nPress ENTER to return to main menu...");
            scanner.nextLine();
        }
    }

    private static void initDefaultNodes() {
        System.out.println("[System] Initializing Microgrid Topology & Registering Nodes...");

        ProsumerNode solar = new ProsumerNode("NOD-SOL-01", "Greenfield Solar Park", "Zone-Alpha", 250.0, "SOLAR", 300.0, 0.11);
        ProsumerNode wind = new ProsumerNode("NOD-WND-02", "Coastal Wind Farm", "Zone-Beta", 400.0, "WIND", 500.0, 0.09);
        BatteryStorageNode bess = new BatteryStorageNode("NOD-BES-03", "Central Storage BESS", "Zone-Alpha", 600.0, 450.0, 200.0, 0.10, 0.14);

        ConsumerNode resCommunity = new ConsumerNode("NOD-RES-04", "Sunset Valley Residential", "Zone-Alpha", 180.0, "RESIDENTIAL", 0.13);
        ConsumerNode indPark = new ConsumerNode("NOD-IND-05", "Apex Industrial Hub", "Zone-Gamma", 350.0, "INDUSTRIAL", 0.16);

        simulationEngine.registerNode(solar);
        simulationEngine.registerNode(wind);
        simulationEngine.registerNode(bess);
        simulationEngine.registerNode(resCommunity);
        simulationEngine.registerNode(indPark);

        System.out.println(ANSI_GREEN + "[System] 5 Microgrid Nodes Registered & Persisted to Database successfully." + ANSI_RESET);
    }

    private static void printMenu() {
        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "=== RESINET CONTROL CENTER MENU ===" + ANSI_RESET);
        System.out.println("1. " + ANSI_YELLOW + "View Microgrid Telemetry Dashboard" + ANSI_RESET);
        System.out.println("2. " + ANSI_YELLOW + "Submit Custom P2P Energy Trade Order" + ANSI_RESET);
        System.out.println("3. " + ANSI_YELLOW + "Toggle Multithreaded Live Grid Simulation" + ANSI_RESET);
        System.out.println("4. " + ANSI_YELLOW + "View Database Transaction Audit Log (JDBC)" + ANSI_RESET);
        System.out.println("5. " + ANSI_YELLOW + "Register New Microgrid Energy Node" + ANSI_RESET);
        System.out.println("6. " + ANSI_YELLOW + "Export Telemetry & Audit Report (File I/O)" + ANSI_RESET);
        System.out.println("7. " + ANSI_YELLOW + "Run Automated System Unit Test Suite" + ANSI_RESET);
        System.out.println("8. " + ANSI_RED + "Exit Platform" + ANSI_RESET);
    }

    private static void viewLiveDashboard() {
        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "--- LIVE MICROGRID TELEMETRY DASHBOARD ---" + ANSI_RESET);
        System.out.printf("%-14s | %-26s | %-12s | %-12s | %-10s%n", "NODE ID", "NAME", "TYPE", "POWER/DEMAND", "STATUS");
        System.out.println("----------------------------------------------------------------------------------");

        List<EnergyNode> nodes = simulationEngine.getAllNodes();
        for (EnergyNode node : nodes) {
            String powerStr = String.format("%.2f kW", node.getCurrentPowerKW());
            if (node instanceof ConsumerNode) {
                powerStr = String.format("Req: %.2f kW", ((ConsumerNode) node).getDemandDemandKW());
            } else if (node instanceof BatteryStorageNode) {
                powerStr = String.format("SOC: %.1f%%", ((BatteryStorageNode) node).getSOCPercentage());
            }

            System.out.printf("%-14s | %-26s | %-12s | %-12s | %s%n",
                    node.getNodeId(), node.getName(), node.getNodeType(), powerStr,
                    node.isActive() ? ANSI_GREEN + "ACTIVE" + ANSI_RESET : ANSI_RED + "INACTIVE" + ANSI_RESET);
        }

        System.out.printf("%nTotal Generation: " + ANSI_GREEN + "%.2f kW" + ANSI_RESET + " | Total Demand: " + ANSI_YELLOW + "%.2f kW" + ANSI_RESET + "%n",
                simulationEngine.calculateTotalGridGenerationKW(),
                simulationEngine.calculateTotalGridDemandKW());
    }

    private static void submitCustomOrder(Scanner scanner) {
        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "--- SUBMIT CUSTOM P2P ENERGY TRADE ORDER ---" + ANSI_RESET);
        try {
            System.out.print("Enter Node ID (e.g. NOD-SOL-01): ");
            String nodeId = scanner.nextLine().trim();

            System.out.print("Order Type [1 for BUY, 2 for SELL]: ");
            String typeChoice = scanner.nextLine().trim();
            TradeOrder.OrderType type = typeChoice.equals("1") ? TradeOrder.OrderType.BUY : TradeOrder.OrderType.SELL;

            System.out.print("Energy Amount in kW: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Price per kWh ($): ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            TradeOrder order = new TradeOrder("ORD-MANUAL-" + System.currentTimeMillis(), nodeId, type, amount, price);
            matchingEngine.submitOrder(order);
            System.out.println(ANSI_GREEN + "[SUCCESS] Order submitted to PriorityQueue Matching Engine!" + ANSI_RESET);
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Failed to submit order: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static void toggleSimulation() {
        if (simThread == null || !simThread.isAlive()) {
            simThread = new Thread(simulationEngine, "GridSimulationThread");
            simThread.start();
            System.out.println(ANSI_GREEN + "[SIMULATION] Multithreaded Grid Engine STARTED! Background thread running..." + ANSI_RESET);
        } else {
            simulationEngine.stopSimulation();
            System.out.println(ANSI_YELLOW + "[SIMULATION] Multithreaded Grid Engine STOPPING..." + ANSI_RESET);
        }
    }

    private static void viewDatabaseTransactions() {
        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "--- PERSISTENT DATABASE TRANSACTIONS (JDBC SQLite) ---" + ANSI_RESET);
        try {
            List<EnergyTransaction> transactions = transactionDAO.getAllTransactions();
            if (transactions.isEmpty()) {
                System.out.println("No transactions recorded in database yet.");
            } else {
                for (EnergyTransaction tx : transactions) {
                    System.out.println(ANSI_GREEN + " • " + tx.toString() + ANSI_RESET);
                }
                System.out.printf("%nTotal Traded Energy: %.2f kW | Total Volume: $%.2f%n",
                        transactionDAO.getTotalEnergyTradedKW(), transactionDAO.getTotalTradingVolumeUSD());
            }
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Database Error: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static void addNewNode(Scanner scanner) {
        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "--- REGISTER NEW MICROGRID NODE ---" + ANSI_RESET);
        try {
            System.out.print("Node Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Node Type [1: Prosumer Solar, 2: Consumer Residential, 3: BESS Storage]: ");
            String typeStr = scanner.nextLine().trim();

            System.out.print("Location Zone (e.g. Zone-Delta): ");
            String zone = scanner.nextLine().trim();

            String id = "NOD-NEW-" + (System.currentTimeMillis() % 10000);
            EnergyNode newNode;

            if ("1".equals(typeStr)) {
                newNode = new ProsumerNode(id, name, zone, 100.0, "SOLAR", 150.0, 0.12);
            } else if ("2".equals(typeStr)) {
                newNode = new ConsumerNode(id, name, zone, 80.0, "RESIDENTIAL", 0.14);
            } else {
                newNode = new BatteryStorageNode(id, name, zone, 300.0, 150.0, 100.0, 0.10, 0.15);
            }

            simulationEngine.registerNode(newNode);
            System.out.println(ANSI_GREEN + "[SUCCESS] Registered node " + id + " to grid & SQLite DB!" + ANSI_RESET);
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Failed to register node: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static void exportAuditReport() {
        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "--- EXPORTING SYSTEM AUDIT REPORT (Java Character Streams) ---" + ANSI_RESET);
        try {
            String path = ReportGenerator.generateAuditReport("logs/ResiNet_Audit_Report.txt");
            System.out.println(ANSI_GREEN + "[SUCCESS] Report generated successfully at: " + path + ANSI_RESET);
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Report Generation failed: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static void runSystemTests() {
        ResiNetTestSuite.main(new String[0]);
    }

    private static void shutdown() {
        if (simThread != null && simThread.isAlive()) {
            simulationEngine.stopSimulation();
        }
        if (loggerThread != null) {
            loggerThread.stopLogger();
        }
    }
}
