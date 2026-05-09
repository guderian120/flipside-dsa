package javaCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single network log entry parsed from a string.
 * This class handles the complex regex parsing required to turn raw text
 * into structured security data.
 */
public class LogEntry {
    // Fields representing the structure of a standard security log
    private LocalDateTime timestamp;
    private String hostname;
    private String process;
    private int pid;
    private String action;
    private String user;
    private String sourceIP;
    private int port;
    private String message;
    private int severity; // Scale of 0-5 (0=Error, 5=Critical)

    // REGEX: Matches "Month Day Time hostname process[pid]: message"
    public static final String SCHEMA_REGEX = "(\\w{3}\\s+\\d+\\s+\\d{2}:\\d{2}:\\d{2})\\s+(\\w+)\\s+(\\w+)\\[(\\d+)\\]:\\s+(.*)";
    public static final String SCHEMA_EXAMPLE = "Jan 18 14:35:05 server sshd[2011]: Message here";

    /**
     * Manual constructor for programmatically creating logs.
     */
    public LogEntry(LocalDateTime timestamp, String hostname, String process, int pid, String action, String user,
            String sourceIP, int port, String message, int severity) {
        this.timestamp = timestamp;
        this.hostname = hostname;
        this.process = process;
        this.pid = pid;
        this.action = action;
        this.user = user;
        this.sourceIP = sourceIP;
        this.port = port;
        this.message = message;
        this.severity = severity;
    }

    /**
     * Validation helper for external classes.
     * @return true if the line matches our security log pattern.
     */
    public static boolean isValidFormat(String logLine) {
        return Pattern.compile(SCHEMA_REGEX).matcher(logLine).find();
    }

    /**
     * Main Constructor: Parses a raw string into a structured object.
     * @param logLine The raw line from the log file.
     * @throws IllegalArgumentException if the format is fundamentally wrong.
     */
    public LogEntry(String logLine) {
        try {
            Pattern pattern = Pattern.compile(SCHEMA_REGEX);
            Matcher matcher = pattern.matcher(logLine);

            if (matcher.find()) {
                // Parse the Timestamp (Linux logs usually don't have the year)
                String tsStr = matcher.group(1);
                int currentYear = LocalDateTime.now().getYear();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss yyyy");
                this.timestamp = LocalDateTime.parse(tsStr + " " + currentYear, formatter);

                // Extract core fields from the main regex
                this.hostname = matcher.group(2);
                this.process = matcher.group(3);
                this.pid = Integer.parseInt(matcher.group(4));
                this.message = matcher.group(5);

                // Heuristic analysis to determine the "Action" and "Severity"
                if (message.contains("Failed password")) {
                    this.action = "Failed password";
                    this.severity = 4; // High severity
                } else if (message.contains("Accepted password")) {
                    this.action = "Accepted password";
                    this.severity = 1; // Normal severity
                } else {
                    this.action = "Info";
                    this.severity = 2;
                }

                // SUB-REGEX: Extract User, IP, and Port from the message body
                Pattern detailPattern = Pattern.compile("for (\\w+) from ([\\d.]+) port (\\d+)");
                Matcher detailMatcher = detailPattern.matcher(message);
                if (detailMatcher.find()) {
                    this.user = detailMatcher.group(1);
                    this.sourceIP = detailMatcher.group(2);
                    this.port = Integer.parseInt(detailMatcher.group(3));
                }
            } else {
                throw new IllegalArgumentException("Log line does not match system schema.");
            }
        } catch (Exception e) {
            // Log fallback if parsing fails partially
            this.timestamp = LocalDateTime.now();
            this.message = logLine;
            this.action = "Error Parsing";
            this.severity = 0;
            // Re-throw if it was a schema violation so the manager can skip it
            if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
        }
    }

    // Getters for analytics...
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getAction() { return action; }
    public String getUser() { return user; }
    public String getSourceIP() { return sourceIP; }
    public int getSeverity() { return severity; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s[%d]: %s (Severity: %d)",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), hostname, process, pid, message, severity);
    }
}
