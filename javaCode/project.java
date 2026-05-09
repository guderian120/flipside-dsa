package javaCode;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;

/**
 * Main Entry Point for the Network Log Analysis System.
 * This class handles user interaction, file selection, and orchestrates the
 * log processing, analysis, and reporting workflow.
 */
public class project {
    // Simulated pool of data for generating random logs if needed
    private static final String[] IPS = { "203.0.113.42", "192.168.1.45", "10.0.0.1", "172.16.0.1", "192.168.1.100" };
    private static final String[] USERS = { "admin", "alice", "bob", "root", "user" };
    private static final Random random = new Random();

    /**
     * Application execution starts here.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize Scanner for console input
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        // Display Application Header
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   NETWORK LOG ANALYSIS SYSTEM (v2.0)");
        System.out.println("=".repeat(50));

        // STEP 1: Select Data Structure
        // Polymorphism allows us to switch the underlying list implementation easily
        System.out.print("\nSelect Data Structure (singly, doubly, circular) [default: doubly]: ");
        String listTypeChoice = scanner.nextLine().trim();
        if (listTypeChoice.isEmpty()) {
            listTypeChoice = "doubly";
        }

        // Initialize the manager with the user's choice
        NetworkLogManager manager = new NetworkLogManager(listTypeChoice);
        System.out.println(">> Initialized with " + manager.getListType().toUpperCase() + " structure.");

        String filePath = "";
        boolean isValid = false;

        // STEP 2: File Selection & Schema Validation Loop
        while (!isValid) {
            System.out.print("\nEnter path to log file (or type 'sample' for default): ");
            filePath = scanner.nextLine().trim();

            // Handle the 'sample' shortcut
            if (filePath.equalsIgnoreCase("sample")) {
                // Check common locations for the sample file to ensure it's found
                if (new File("sample_logs.txt").exists()) {
                    filePath = "sample_logs.txt";
                } else if (new File("javaCode/sample_logs.txt").exists()) {
                    filePath = "javaCode/sample_logs.txt";
                } else {
                    System.out.println("!! Error: sample_logs.txt not found in root or javaCode/ directory.");
                    continue;
                }
            }

            // Ensure the file physically exists before attempting to read
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("!! Error: File not found at " + filePath);
                continue;
            }

            // Perform Schema Validation to ensure the log format matches our regex
            System.out.println(">> Validating schema...");
            if (manager.validateLogFile(filePath)) {
                System.out.println(">> Schema validated successfully!");
                isValid = true;
            } else {
                System.out.println("\n[!] ALERT: Invalid Log Schema Detected!");
                System.out.println("    The system requires standard Linux/SSH log formats.");
                System.out.println("    Required Format: " + LogEntry.SCHEMA_EXAMPLE);
                System.out.print("    Would you like to try a different file? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.println("Exiting system.");
                    return;
                }
            }
        }

        // STEP 3: Processing & Timing
        // We track the start time to calculate performance metrics
        long startTime = System.currentTimeMillis();
        System.out.println("\n--- Processing Logs ---");
        manager.readLogsFromFile(filePath);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // Total processing time in ms
        System.out.println(">> Total logs loaded: " + manager.getLogCount());

        // STEP 4: Security Analysis
        // Scans the loaded data for failed logins and suspicious patterns
        System.out.println("\n--- Performing Real-time Security Analysis ---");
        manager.detectSuspiciousActivity();

        // STEP 5: Display Performance Metadata
        // Prints the Big-O complexity of the currently selected data structure
        System.out.println("\n" + manager.getComplexityReport());

        // STEP 6: Report Generation
        // Exports all data and analytics to a premium HTML format
        System.out.println("\n--- Generating Industry Standard Report ---");

        // Prepare analytics map for the report generator
        java.util.Map<String, String> analytics = new java.util.HashMap<>();
        analytics.put("Processing Time", duration + " ms");
        analytics.put("Throughput", String.format("%.2f logs/sec", (manager.getLogCount() / (duration / 1000.0))));
        analytics.put("Memory Usage",
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " MB");
        analytics.put("Search Efficiency",
                manager.getListType().equalsIgnoreCase("singly") ? "Low (Linear)" : "Medium (Bidirectional)");

        // Create a unique filename using the current timestamp
        String reportName = "Reports/Security_Report_" + System.currentTimeMillis() + ".html";
        ReportGenerator.generateHtmlReport(manager, reportName, analytics);

        // Final completion summary
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  EXECUTION COMPLETE");
        System.out.println("  Report exported to: " + reportName);
        System.out.println("  Processing Time: " + duration + "ms");
        System.out.println("=".repeat(50));

        System.out.println("\nPress Enter to exit.");
        scanner.nextLine();
        scanner.close(); // Clean up resources
    }

    /**
     * Utility to generate a random LogEntry object.
     * Useful for stress testing or simulations.
     * 
     * @return A randomized LogEntry
     */
    private static LogEntry generateRandomLog() {
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(random.nextInt(60));
        String hostname = "server";
        String process = "sshd";
        int pid = 2000 + random.nextInt(100);
        String user = USERS[random.nextInt(USERS.length)];
        String sourceIP = IPS[random.nextInt(IPS.length)];
        int port = 40000 + random.nextInt(20000);
        boolean isFailed = random.nextBoolean();
        String action = isFailed ? "Failed password" : "Accepted password";
        String message = action + " for " + user + " from " + sourceIP + " port " + port;
        int severity = isFailed ? 4 : 1;

        return new LogEntry(timestamp, hostname, process, pid, action, user, sourceIP, port, message, severity);
    }
}
