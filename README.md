# Network Log Analysis System (v2.0)

A professional-grade Java application for parsing, analyzing, and reporting network security incidents using various Linked List data structures.

## 🚀 Quick Start

### 1. Prerequisites
Ensure you have the Java Development Kit (JDK) installed (version 8 or higher).

### 2. Compilation
Open your terminal in the root directory (`FlipSide`) and run:
```bash
javac javaCode/*.java
```

### 3. Execution
Run the main project class:
```bash
java javaCode.project
```

---

## 🛠️ How to Use

1.  **Select Data Structure**: Upon launch, choose between `singly`, `doubly`, or `circular`.
    *   *Tip*: Use `doubly` for the best balance of speed and functionality.
2.  **Provide Log File**: Enter the path to your log file. 
    *   You can type `sample` to use the provided `sample_logs.txt`.
3.  **Validation**: The system will automatically check if your file follows the required schema.
4.  **View Report**: Once processing is complete, a professional HTML report (e.g., `Security_Report_12345.html`) will be generated in the root directory.

---

## 📊 Key Features

### 🔍 Real-time Security Analysis
*   **Brute-Force Detection**: Automatically flags IP addresses with more than 3 failed login attempts.
*   **Severity Scoring**: Categorizes incidents by severity (0-5) based on log content.
*   **IP Tracking**: Identifies the most active and suspicious source IPs.

### 📈 Technical Analytics
*   **Complexity Metrics**: Displays Time and Space complexity (Big O) for your chosen data structure.
*   **Performance Tracking**: Shows processing time (ms), throughput (logs/sec), and memory footprint.

### 📄 Premium Reporting
*   **HTML/PDF Export**: Generates a modern, print-ready report with gradient headers and interactive-style tables.
*   **Sorting**: Automatically sorts the incident log by severity, bringing critical threats to the top.

---

## 📁 Directory Structure

*   `project.java`: The main user interface and workflow controller.
*   `NetworkLogManager.java`: The central engine for log storage and analysis.
*   `LogEntry.java`: The parsing logic that turns raw text into security objects.
*   `ReportGenerator.java`: The reporting engine that creates HTML documents.
*   `LinkedList.java`: The interface defining the list "contract."
*   `SinglyLinkedList.java`, `DoublyLinkedList.java`, `CircularLinkedList.java`: Concrete data structure implementations.
*   `sample_logs.txt`: An example log file for testing.

---

## ⚠️ Log Schema Requirement
The system expects standard Linux/SSH log formats:
`Jan 18 14:35:05 server sshd[2011]: Failed password for admin from 203.0.113.42 port 44512`

---
*Developed for advanced network security monitoring and data structure analysis.*
