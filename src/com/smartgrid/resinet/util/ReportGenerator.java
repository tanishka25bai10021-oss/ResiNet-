package com.smartgrid.resinet.util;

import com.smartgrid.resinet.db.EnergyNodeDAO;
import com.smartgrid.resinet.db.TransactionDAO;
import com.smartgrid.resinet.model.EnergyTransaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReportGenerator - Utility class for exporting microgrid telemetry & transaction audit reports.
 * Uses Character Streams (FileWriter, BufferedWriter, PrintWriter) to fulfill CSE2006 Unit 4.
 */
public class ReportGenerator {

    public static String generateAuditReport(String outputFilePath) throws IOException {
        File file = new File(outputFilePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        EnergyNodeDAO nodeDAO = new EnergyNodeDAO();
        TransactionDAO txDAO = new TransactionDAO();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.println("===============================================================================");
            writer.println("             RESINET SMART MICROGRID AUDIT & TELEMETRY REPORT                  ");
            writer.println("===============================================================================");
            writer.println("Generated At : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("System Name  : ResiNet Multi-Threaded P2P Energy Trading Platform");
            writer.println("Course Code  : CSE2006 - Programming in Java");
            writer.println("===============================================================================\n");

            writer.println("1. MICROGRID SYSTEM METRICS");
            writer.println("-------------------------------------------------------------------------------");
            try {
                int activeNodes = nodeDAO.getActiveNodeCount();
                double totalTradedKW = txDAO.getTotalEnergyTradedKW();
                double totalVolumeUSD = txDAO.getTotalTradingVolumeUSD();

                writer.printf("Total Active Grid Nodes     : %d%n", activeNodes);
                writer.printf("Total Energy Traded (kW)   : %.2f kW%n", totalTradedKW);
                writer.printf("Total Trading Volume ($)   : $%.2f%n", totalVolumeUSD);
            } catch (SQLException e) {
                writer.println("Error querying system metrics: " + e.getMessage());
            }

            writer.println("\n2. REGISTERED ENERGY NODES SUMMARY");
            writer.println("-------------------------------------------------------------------------------");
            try {
                List<String> nodeSummaries = nodeDAO.getAllNodeSummaries();
                if (nodeSummaries.isEmpty()) {
                    writer.println("No nodes registered in database.");
                } else {
                    for (String summary : nodeSummaries) {
                        writer.println(" • " + summary);
                    }
                }
            } catch (SQLException e) {
                writer.println("Error fetching node list: " + e.getMessage());
            }

            writer.println("\n3. RECENT P2P TRANSACTIONS LOG");
            writer.println("-------------------------------------------------------------------------------");
            try {
                List<EnergyTransaction> transactions = txDAO.getAllTransactions();
                if (transactions.isEmpty()) {
                    writer.println("No transactions recorded yet.");
                } else {
                    for (EnergyTransaction tx : transactions) {
                        writer.println(" • " + tx.toString());
                    }
                }
            } catch (SQLException e) {
                writer.println("Error fetching transaction logs: " + e.getMessage());
            }

            writer.println("\n===============================================================================");
            writer.println("                             END OF REPORT                                     ");
            writer.println("===============================================================================");
            writer.flush();
        }

        return file.getAbsolutePath();
    }
}
