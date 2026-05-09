
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

/**
 * The NetworkLogManager acts as a controller for the log storage and analysis.
 * It uses the Strategy Pattern to swap between different Linked List
 * implementations.
 */
public class NetworkLogManager {
    // The internal list used to store LogEntry objects.
    // Defined by an interface to support polymorphism.
    private LinkedList<LogEntry> logList;
    private String listType;
    private int maxSize; // Used for circular buffer rotation logic

    /**
     * Default constructor with no size limit.
     * 
     * @param listType The type of linked list to use (singly, doubly, circular).
     */
    public NetworkLogManager(String listType) {
        this(listType, -1); // -1 signifies an unlimited buffer
    }

    /**
     * Main constructor for initializing the manager.
     * 
     * @param listType The type of linked list to use.
     * @param maxSize  Maximum number of logs for circular storage.
     */
    public NetworkLogManager(String listType, int maxSize) {
        this.listType = listType;
        this.maxSize = maxSize;
        initializeList(listType);
    }

    /**
     * Factory-style method to instantiate the concrete list implementation.
     */
    private void initializeList(String type) {
        switch (type.toLowerCase()) {
            case "singly":
                logList = new SinglyLinkedList<>();
                break;
            case "doubly":
                logList = new DoublyLinkedList<>();
                break;
            case "circular":
                logList = new CircularLinkedList<>();
                break;
            default:
                logList = new SinglyLinkedList<>();
        }
    }

    /**
     * Adds a log entry to the list.
     * If the list is a full circular buffer, it automatically removes the oldest
     * log.
     */
    public void addLog(LogEntry log) {
        // Circular buffer eviction logic:
        // If we have a size limit and we've reached it, and we are in circular mode...
        if (maxSize > 0 && logList.size() >= maxSize && listType.equalsIgnoreCase("circular")) {
            List<LogEntry> logs = logList.toList();
            if (!logs.isEmpty()) {
                // Remove the item at the head of the list (the oldest)
                logList.remove(logs.get(0));
            }
        }
        logList.add(log);
    }

    /**
     * Removes a specific log entry.
     */
    public void removeLog(LogEntry log) {
        logList.remove(log);
    }

    public int getLogCount() {
        return logList.size();
    }

    /**
     * Returns a standard Java List containing all log entries.
     */
    public List<LogEntry> getAllLogs() {
        return logList.toList();
    }

    // ==========================================
    // ANALYSIS METHODS (Using Java Stream API)
    // ==========================================

    /**
     * Groups logs by source IP and counts them.
     * Useful for identifying high-traffic nodes.
     */
    public Map<String, Long> getLogsBySourceIP() {
        return logList.toList().stream()
                .collect(Collectors.groupingBy(LogEntry::getSourceIP, Collectors.counting()));
    }

    /**
     * Identifies logs with a high security severity level.
     */
    public List<LogEntry> getHighSeverityLogs(int minSeverity) {
        return logList.toList().stream()
                .filter(log -> log.getSeverity() >= minSeverity)
                .collect(Collectors.toList());
    }

    /**
     * Scans for suspicious patterns like brute force login attempts (>3 failures).
     */
    public void detectSuspiciousActivity() {
        // Find all failed login attempts grouped by IP
        Map<String, Long> failedLogins = logList.toList().stream()
                .filter(log -> log.getAction().startsWith("Failed"))
                .collect(Collectors.groupingBy(LogEntry::getSourceIP, Collectors.counting()));

        System.out.println("Suspicious Activity Analysis:");
        System.out.println("Brute-force detection (>3 failures):");
        failedLogins.entrySet().stream()
                .filter(entry -> entry.getValue() > 3)
                .forEach(entry -> System.out
                        .println("  ALERT: IP " + entry.getKey() + " has " + entry.getValue() + " failed attempts!"));
    }

    // ==========================================
    // FILE I/O AND PERFORMANCE METRICS
    // ==========================================

    /**
     * Validates if a file follows the expected logging schema.
     * Checks the first 5 lines to confirm format compatibility.
     */
    public boolean validateLogFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int linesChecked = 0;
            while ((line = reader.readLine()) != null && linesChecked < 5) {
                if (line.trim().isEmpty())
                    continue;
                // Use the static validator in LogEntry
                if (!LogEntry.isValidFormat(line.trim())) {
                    return false;
                }
                linesChecked++;
            }
            return linesChecked > 0;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads and parses logs from a text file into the linked list.
     */
    public void readLogsFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        LogEntry log = new LogEntry(line.trim());
                        addLog(log);
                    } catch (IllegalArgumentException e) {
                        // Skip lines that don't match the schema
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Generates a textual representation of the Time and Space complexity
     * for the currently active data structure.
     */
    public String getComplexityReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- COMPLEXITY ANALYSIS METRICS ---\n");
        sb.append("Current Structure: ").append(listType.toUpperCase()).append("\n");

        switch (listType.toLowerCase()) {
            case "singly":
                sb.append("  * Addition: O(n) - Linear traversal required to find tail\n");
                sb.append("  * Search:   O(n) - Linear scan required\n");
                sb.append("  * Memory:   O(n) - Single reference per node\n");
                break;
            case "doubly":
                sb.append("  * Addition: O(1) - Constant time using tail pointer\n");
                sb.append("  * Search:   O(n) - Bidirectional scan available\n");
                sb.append("  * Memory:   O(n) - Dual references per node (higher overhead)\n");
                break;
            case "circular":
                sb.append("  * Addition: O(1) - Constant time using tail pointer\n");
                sb.append("  * Rotation: O(1) - Constant time to wrap tail to head\n");
                sb.append("  * Memory:   O(n) - Single reference per node\n");
                break;
        }
        return sb.toString();
    }

    public String getListType() {
        return listType;
    }
}