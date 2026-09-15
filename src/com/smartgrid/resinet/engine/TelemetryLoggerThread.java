package com.smartgrid.resinet.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Multithreaded Telemetry Logger using Java I/O Streams (Character Streams: BufferedWriter & FileWriter).
 * Fulfills CSE2006 Unit 4 (Java List & I/O Streams).
 */
public class TelemetryLoggerThread extends Thread {
    private final BlockingQueue<String> logQueue;
    private final File logFile;
    private volatile boolean running;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TelemetryLoggerThread(String logFilePath) {
        super("TelemetryLoggerThread");
        this.logQueue = new LinkedBlockingQueue<>();
        this.logFile = new File(logFilePath);
        this.running = true;
        
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    public void logEvent(String message) {
        String timestamped = String.format("[%s] %s", LocalDateTime.now().format(formatter), message);
        logQueue.offer(timestamped);
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("=== ResiNet Smart Microgrid Telemetry Session Started ===");
            writer.newLine();
            writer.flush();

            while (running || !logQueue.isEmpty()) {
                try {
                    String logMessage = logQueue.poll(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                    if (logMessage != null) {
                        writer.write(logMessage);
                        writer.newLine();
                        writer.flush();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            writer.write("=== ResiNet Telemetry Session Terminated ===");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("[TelemetryLogger] File I/O Error: " + e.getMessage());
        }
    }

    public void stopLogger() {
        this.running = false;
    }
}
