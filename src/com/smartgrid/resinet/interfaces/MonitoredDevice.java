package com.smartgrid.resinet.interfaces;

/**
 * Interface representing telemetry monitoring and status reporting capabilities.
 */
public interface MonitoredDevice {
    String getStatusReport();
    double getHealthIndex(); // 0.0 to 1.0 (100% health)
    boolean isOperational();
    void performSelfDiagnostic();
}
