# Project Problem Statement & Scope: ResiNet

## 1. Problem Statement
Modern electrical distribution grids face severe challenges integrating distributed renewable energy sources (DERs) such as residential solar panels, small-scale wind turbines, and battery energy storage systems (BESS). Traditional centralized energy grids are inefficient at managing localized power fluctuations, resulting in transformer congestion, transmission losses, and volatile utility tariffs. 

Furthermore, prosumers (entities that both produce and consume energy) lack an automated, peer-to-peer (P2P) clearinghouse to trade localized energy surpluses directly with neighboring consumers at dynamic market-driven prices.

## 2. Project Scope
**ResiNet** is a multi-threaded smart microgrid P2P energy trading and load balancing system built in Java. It provides a real-time, decentralized energy market simulation that:
- Connects diverse microgrid nodes (prosumers, industrial consumers, residential units, and battery storage).
- Continuously executes a concurrent order-matching algorithm using thread-safe priority queues.
- Monitors transformer load capacity and enforces dynamic safety limits with custom exception handling.
- Persists all node topologies and financial clearing logs into an embedded SQLite database using JDBC.
- Streams live telemetry events to disk using character/byte I/O streams for auditing and analytics.

## 3. Target Users
- **Microgrid Operators & Energy Aggregators**: To simulate, balance, and monitor renewable energy distribution across localized grid zones.
- **Prosumers & Battery Owners**: To monetize excess solar/wind energy by automatically posting sell bids on the peer-to-peer clearinghouse.
- **Commercial & Residential Consumers**: To procure clean, green power from neighborhood producers at lower prices than traditional utility tariffs.
- **Academic Evaluators**: To assess multi-threaded Java design patterns, JDBC database integration, custom exception hierarchies, and I/O stream processing.

## 4. High-Level Features
1. **Polymorphic Microgrid Node Architecture**: Encapsulation and abstraction of Prosumer, Consumer, and Battery Storage nodes with telemetry tracking contracts (`Tradeable`, `MonitoredDevice`).
2. **Multithreaded P2P Order Matching Engine**: PriorityQueue-based order matching algorithm prioritizing highest buy bids and lowest sell asks concurrently.
3. **Live Grid Simulation Engine**: Multi-threaded generation fluctuation, dynamic demand modeling, and `ReentrantLock`-based grid load management.
4. **Persistent JDBC Storage & Analytics**: Embedded SQLite database layer featuring PreparedStatement CRUD operations and SQL aggregate financial queries.
5. **Character/Byte Stream File I/O Exporter**: Automated generation of text-based telemetry audit logs and system telemetry reports.
6. **Automated Unit Test Runner**: Standalone test suite (`ResiNetTestSuite`) verifying system correctness, multithreaded concurrency safety, and database operations.
