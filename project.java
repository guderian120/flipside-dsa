package javaCode;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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
        System.out.print("\nSelect Data Structure (singly, doubly, circular, all) [default: all]: ");
        String listTypeChoice = scanner.nextLine().trim().toLowerCase();
        if (listTypeChoice.isEmpty()) {
            listTypeChoice = "all";
        }

        // Initialize managers
        List<NetworkLogManager> managers = new ArrayList<>();
        if (listTypeChoice.equals("all")) {
            managers.add(new NetworkLogManager("singly"));
            managers.add(new NetworkLogManager("doubly"));
            managers.add(new NetworkLogManager("circular"));
            System.out.println(">> Initialized with ALL structures (Singly, Doubly, Circular).");
        } else {
            managers.add(new NetworkLogManager(listTypeChoice));
            System.out.println(">> Initialized with " + listTypeChoice.toUpperCase() + " structure.");
        }

        String inputPath = "";
        boolean isValid = false;
        List<File> logFiles = new ArrayList<>();

        // STEP 2: File Selection & Schema Validation Loop
        while (!isValid) {
            System.out.print("\nEnter path to log file or directory (or type 'sample' for default): ");
            inputPath = scanner.nextLine().trim();

            // Handle the 'sample' shortcut
            if (inputPath.equalsIgnoreCase("sample")) {
                // Check common locations for the sample file to ensure it's found
                if (new File("sample_logs.txt").exists()) {
                    inputPath = "sample_logs.txt";
                } else if (new File("logs/sample_logs.txt").exists()) {
                    inputPath = "logs/sample_logs.txt";
                } else {
                    System.out.println("!! Error: sample_logs.txt not found in root or javaCode/ directory.");
                    continue;
                }
            }

            // Ensure the path physically exists before attempting to read
            File file = new File(inputPath);
            if (!file.exists()) {
                System.out.println("!! Error: Path not found at " + inputPath);
                continue;
            }

            logFiles.clear();
            if (file.isDirectory()) {
                File[] files = file.listFiles((dir, name) -> name.endsWith(".log") || name.endsWith(".txt"));
                if (files != null) {
                    logFiles.addAll(Arrays.asList(files));
                }
                if (logFiles.isEmpty()) {
                    System.out.println("!! Error: No .log or .txt files found in directory.");
                    continue;
                }
            } else {
                logFiles.add(file);
            }

            // Perform Schema Validation to ensure the log format matches our regex
            System.out.println(">> Validating schema...");
            boolean anyValid = false;
            for (File logFile : logFiles) {
                if (managers.get(0).validateLogFile(logFile.getAbsolutePath())) {
                    anyValid = true;
                    break;
                }
            }

            if (anyValid) {
                System.out.println(">> Schema validated successfully!");
                isValid = true;
            } else {
                System.out.println("\n[!] ALERT: Invalid Log Schema Detected in all files!");
                System.out.println("    The system requires standard Linux/SSH log formats.");
                System.out.println("    Required Format: " + LogEntry.SCHEMA_EXAMPLE);
                System.out.print("    Would you like to try a different path? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.println("Exiting system.");
                    return;
                }
            }
        }

        // STEP 3: Processing & Timing
        System.out.println("\n--- Processing Logs ---");

        java.util.Map<String, String> analytics = new java.util.LinkedHashMap<>();

        for (NetworkLogManager manager : managers) {
            long startTime = System.currentTimeMillis();
            for (File logFile : logFiles) {
                manager.readLogsFromFile(logFile.getAbsolutePath());
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime; // Total processing time in ms

            String type = manager.getListType().toUpperCase();
            System.out.println(
                    ">> [" + type + "] Total logs loaded: " + manager.getLogCount() + " in " + duration + "ms");

            analytics.put(type + " Processing Time", duration + " ms");
            double throughput = duration == 0 ? manager.getLogCount() : (manager.getLogCount() / (duration / 1000.0));
            analytics.put(type + " Throughput", String.format("%.2f logs/sec", throughput));
        }

        // Memory Usage
        analytics.put("Overall Memory Usage",
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " MB");

        // STEP 4: Security Analysis
        // Scans the loaded data for failed logins and suspicious patterns
        System.out.println("\n--- Performing Real-time Security Analysis ---");
        // We only need to run detection on one manager since data is same
        managers.get(0).detectSuspiciousActivity();

        // STEP 5: Display Performance Metadata
        // Prints the Big-O complexity of the currently selected data structure
        for (NetworkLogManager manager : managers) {
            System.out.println("\n" + manager.getComplexityReport());
        }

        // STEP 6: Report Generation
        // Exports all data and analytics to a premium HTML format
        System.out.println("\n--- Generating Industry Standard Report ---");

        // Create a unique filename using the current timestamp
        File reportsDir = new File("Reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdir();
        }
        String reportName = "Reports/Security_Report_" + System.currentTimeMillis() + ".html";
        ReportGenerator.generateHtmlReport(managers, reportName, analytics);

        // Final completion summary
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  EXECUTION COMPLETE");
        System.out.println("  Report exported to: " + reportName);
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
