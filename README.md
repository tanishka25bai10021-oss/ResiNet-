# ResiNet: Multi-Threaded Smart Microgrid P2P Energy Trading Engine

[![Java Version](https://img.shields.io/badge/Java-26-orange.svg)](https://java.com)
[![Course](https://img.shields.io/badge/Course-CSE2006%20Programming%20in%20Java-blue.svg)]()
[![Database](https://img.shields.io/badge/Database-SQLite%203%20JDBC-green.svg)]()
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

> **ResiNet** is an advanced, high-throughput Java application that simulates a decentralized smart microgrid energy market. It features real-time peer-to-peer (P2P) renewable energy trading, multi-threaded grid load balancing, JDBC database transaction logging, custom exception management, and Java I/O telemetry streaming.

---

## 🌟 Key Features

- **Polymorphic Microgrid Modeling**: OOP hierarchy representing `ProsumerNode` (Solar/Wind), `ConsumerNode` (Residential/Industrial), and `BatteryStorageNode` (BESS) implementing `Tradeable` and `MonitoredDevice` interfaces.
- **Concurrent P2P Matching Engine**: High-performance priority queue matching algorithm matching buy bids and sell asks asynchronously with sub-millisecond execution.
- **Multithreaded Simulation & Lock Control**: Live simulation engine utilizing Java `Thread` execution, `ReentrantLock` capacity locks, and dynamic solar/wind generation modeling.
- **Custom Exception Handling**: Custom hierarchy handling grid transformer overloads (`GridOverloadException`), supply deficits (`InsufficientEnergyException`), and order invalidations (`InvalidTradeOrderException`).
- **JDBC Persistence Layer**: Embedded SQLite database (`data/resinet_grid.db`) managed via Singleton `DatabaseManager` and `PreparedStatement` DAOs.
- **Java I/O Stream Reporting**: Asynchronous background telemetry logger (`BufferedWriter`) and comprehensive audit report generator (`PrintWriter`).
- **Zero-Dependency Quick Launcher**: Pre-bundled SQLite JDBC and SLF4J libraries under `lib/` with automated batch and shell build scripts.

---

## 🏗️ System Architecture

```text
                               +-----------------------------+
                               |     ResiNet Main CLI Menu   |
                               +--------------+--------------+
                                              |
                     +------------------------+------------------------+
                     |                                                 |
       +-------------v--------------+                   +--------------v-------------+
       |   GridSimulationEngine     |                   |    OrderMatchingEngine     |
       | (Multithreaded Loop & Locks)|                   |  (PriorityQueue Matching)   |
       +-------------+--------------+                   +--------------+-------------+
                     |                                                 |
         +-----------+-----------+                         +-----------+-----------+
         |                       |                         |                       |
+--------v-------+      +--------v-------+        +--------v-------+      +--------v-------+
|  ProsumerNode  |      |  ConsumerNode  |        | EnergyNodeDAO  |      | TransactionDAO |
|  (Solar/Wind)  |      | (Res/Industrial|        |  (SQLite DB)   |      |  (SQLite DB)   |
+----------------+      +----------------+        +----------------+      +----------------+
```

---

## 💻 Tech Stack & Java Syllabus Alignment (CSE2006)

| Course Module | Project Technical Component |
| :--- | :--- |
| **Java Flow Control & Data Types** | Switch routing, loops, arrays, Collections (`PriorityQueue`, `ConcurrentHashMap`, `ArrayList`, `Stack`). |
| **OOP & Design Patterns** | Abstraction (`EnergyNode`), Interfaces (`Tradeable`, `MonitoredDevice`), Singleton (`DatabaseManager`), DAO Pattern (`EnergyNodeDAO`, `TransactionDAO`). |
| **Exception Handling** | Custom checked exceptions (`ResiNetException`, `GridOverloadException`, `InsufficientEnergyException`, `InvalidTradeOrderException`). |
| **Multithreading & Synchronization** | Multi-threaded simulation (`Thread`, `Runnable`), `ReentrantLock` concurrency safety, `BlockingQueue` telemetry logger. |
| **Java I/O Streams** | Character streams (`FileWriter`, `BufferedWriter`, `PrintWriter`) for audit logging and report exporting. |
| **Database Applications (JDBC)** | SQLite JDBC integration, `PreparedStatement`, transaction DDL/DML, and SQL aggregation queries. |

---

## 🚀 Installation & Quick Start

### Prerequisites
- Java Development Kit (JDK 17 or higher, tested on JDK 26).
- Git (optional).

### Running on Windows
Double-click `compile_and_run.bat` or execute in PowerShell / CMD:
```cmd
compile_and_run.bat
```

### Running on Linux / macOS
Grant execution permissions and run `compile_and_run.sh`:
```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

---

## 🧪 Running Automated Unit Tests

ResiNet includes a built-in automated test runner (`ResiNetTestSuite`) that tests OOP contracts, exception throws, priority queue matching, and JDBC database operations.

```cmd
java -cp "bin;lib/*" com.smartgrid.resinet.test.ResiNetTestSuite
```

### Expected Test Output:
```text
=================================================================
           RUNNING RESINET SYSTEM UNIT TEST SUITE               
=================================================================

--- Test Category: OOP & Polymorphism ---
[PASS] Prosumer polymorphic type check
[PASS] Consumer polymorphic type check
[PASS] Encapsulation power getter check

--- Test Category: Exception Handling ---
[PASS] Exception error code match
[PASS] InsufficientEnergyException thrown correctly on power deficit

--- Test Category: PriorityQueue & Order Matching ---
[OrderMatcher] MATCHED: Seller P-TEST-01 -> Buyer C-TEST-01 (50.00 kW @ $0.120/kWh)
[PASS] Order matching engine successfully paired buy/sell orders
[PASS] InvalidTradeOrderException thrown on negative power order

--- Test Category: JDBC Database & DAO ---
[PASS] JDBC PreparedStatement inserted node into SQLite successfully
[PASS] JDBC SELECT count retrieved active nodes from SQLite

-----------------------------------------------------------------
TEST SUITE RESULTS: 9 PASSED | 0 FAILED | TOTAL: 9
-----------------------------------------------------------------
```

---

## 📁 Repository Folder Structure

```text
smart-microgrid-energy-dispatcher/
├── bin/                              # Compiled .class files
├── data/                             # SQLite persistent database (resinet_grid.db)
├── lib/                              # Embedded JDBC & Logging dependencies
│   ├── sqlite-jdbc.jar
│   ├── slf4j-api-2.0.12.jar
│   └── slf4j-simple-2.0.12.jar
├── logs/                             # I/O log telemetry output & exported reports
├── src/
│   └── com/smartgrid/resinet/
│       ├── db/                       # DatabaseManager, EnergyNodeDAO, TransactionDAO
│       ├── engine/                   # GridSimulationEngine, OrderMatchingEngine, TelemetryLoggerThread
│       ├── exceptions/               # Custom exception hierarchy
│       ├── interfaces/               # Tradeable, MonitoredDevice
│       ├── model/                    # EnergyNode, ProsumerNode, ConsumerNode, BatteryStorageNode, TradeOrder, EnergyTransaction
│       ├── test/                     # ResiNetTestSuite
│       ├── util/                     # ReportGenerator
│       └── Main.java                 # Interactive CLI Entry Point
├── compile_and_run.bat               # Windows launcher script
├── compile_and_run.sh                # Linux launcher script
├── statement.md                      # Problem statement & project scope document
└── README.md                         # Project documentation
```

---

## 📄 License & Academic Submission
Developed for **CSE2006 Programming in Java** academic project submission.
Original Work by Student. Free to evaluate and inspect.
