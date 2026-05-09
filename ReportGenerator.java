package javaCode;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportGenerator is responsible for converting the internal log data
 * into a professional, human-readable HTML format.
 */
public class ReportGenerator {

    /**
     * Generates an HTML report with CSS styling.
     * 
     * @param manager    The manager holding the log data.
     * @param outputPath Where to save the resulting .html file.
     * @param analytics  Performance metrics (timing, memory, etc.) to include.
     */
    public static void generateHtmlReport(NetworkLogManager manager, String outputPath, Map<String, String> analytics) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            // HTML Boilerplate and Document Header
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='en'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <title>Network Security Incident Report</title>");

            // EMBEDDED CSS: Creates the modern, professional look of the report
            writer.println("    <style>");
            writer.println(
                    "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 1000px; margin: 0 auto; padding: 20px; background-color: #f4f7f6; }");
            writer.println(
                    "        .header { background: #1a2a6c; background: linear-gradient(to right, #b21f1f, #fdbb2d, #1a2a6c); color: #fff; padding: 40px; border-radius: 8px 8px 0 0; text-align: center; }");
            writer.println(
                    "        .container { background: #fff; padding: 30px; border-radius: 0 0 8px 8px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }");
            writer.println(
                    "        h1, h2 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }");

            // Grid layout for statistics cards
            writer.println(
                    "        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 15px; margin: 20px 0; }");
            writer.println(
                    "        .stat-card { background: #f8f9fa; padding: 15px; border-radius: 8px; text-align: center; border-top: 4px solid #3498db; transition: transform 0.2s; }");
            writer.println("        .stat-card:hover { transform: translateY(-5px); }");

            // Analytics section formatting (Green box)
            writer.println(
                    "        .analytics-section { background: #eef2f3; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 5px solid #2ecc71; }");

            // Table styling for the log listing
            writer.println("        table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
            writer.println("        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            writer.println("        th { background-color: #f1f4f6; color: #2c3e50; font-weight: 600; }");

            // Dynamic coloring for high severity items
            writer.println(
                    "        .severity-high { color: #fff; background: #e74c3c; padding: 4px 8px; border-radius: 4px; font-size: 0.9em; }");
            writer.println(
                    "        .severity-med { color: #fff; background: #f39c12; padding: 4px 8px; border-radius: 4px; font-size: 0.9em; }");

            // Complexity metrics box (Console style)
            writer.println(
                    "        .complexity-box { background: #2d3436; color: #00ff00; padding: 20px; border-radius: 6px; margin: 20px 0; white-space: pre-wrap; font-family: 'Consolas', monospace; font-size: 0.9em; }");
            writer.println(
                    "        .footer { margin-top: 30px; text-align: center; font-size: 0.8em; color: #95a5a6; border-top: 1px solid #eee; padding-top: 20px; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");

            // Report Main Header
            writer.println("    <div class='header'>");
            writer.println("        <h1>Network Security Analytics Report</h1>");
            writer.println("        <p>Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>");
            writer.println("    </div>");

            writer.println("    <div class='container'>");

            // SECTION 1: SYSTEM PERFORMANCE ANALYTICS
            // Iterates through the analytics map provided by the project class
            writer.println("        <h2>1. System Performance & Analytics</h2>");
            writer.println("        <div class='analytics-section'>");
            for (Map.Entry<String, String> entry : analytics.entrySet()) {
                writer.println("            <div style='margin-bottom: 10px;'><strong>" + entry.getKey() + ":</strong> "
                        + entry.getValue() + "</div>");
            }
            writer.println("        </div>");

            // SECTION 2: HIGH-LEVEL STATISTICS
            writer.println("        <div class='stats-grid'>");
            writer.println(
                    "            <div class='stat-card'><h3>Log Count</h3><p>" + manager.getLogCount() + "</p></div>");
            writer.println("            <div class='stat-card'><h3>Critical Threats</h3><p>"
                    + manager.getHighSeverityLogs(4).size() + "</p></div>");
            writer.println("            <div class='stat-card'><h3>Attack Sources</h3><p>"
                    + manager.getLogsBySourceIP().size() + "</p></div>");
            writer.println("            <div class='stat-card'><h3>Data Structure</h3><p>"
                    + manager.getListType().toUpperCase() + "</p></div>");
            writer.println("        </div>");

            // SECTION 3: TECHNICAL PERFORMANCE (BIG O)
            writer.println("        <h2>2. Technical Performance Analysis</h2>");
            writer.println("        <div class='complexity-box'>" + manager.getComplexityReport() + "</div>");

            // SECTION 4: DETAILED SECURITY LOGS
            writer.println("        <h2>3. Detailed Security Log listing</h2>");
            writer.println("        <table>");
            writer.println(
                    "            <thead><tr><th>Timestamp</th><th>Source IP</th><th>User</th><th>Action</th><th>Severity</th></tr></thead>");
            writer.println("            <tbody>");

            // Get all logs and sort them by severity (High first) before printing
            List<LogEntry> logs = manager.getAllLogs();
            logs.sort((a, b) -> Integer.compare(b.getSeverity(), a.getSeverity()));

            for (LogEntry log : logs) {
                // Determine styling based on severity level
                String sevClass = log.getSeverity() >= 4 ? "severity-high"
                        : (log.getSeverity() >= 3 ? "severity-med" : "");
                writer.println("                <tr>");
                writer.println("                    <td>"
                        + log.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "</td>");
                writer.println("                    <td>" + (log.getSourceIP() != null ? log.getSourceIP() : "Internal")
                        + "</td>");
                writer.println(
                        "                    <td>" + (log.getUser() != null ? log.getUser() : "System") + "</td>");
                writer.println("                    <td>" + log.getAction() + "</td>");
                writer.println(
                        "                    <td><span class='" + sevClass + "'>" + log.getSeverity() + "</span></td>");
                writer.println("                </tr>");
            }
            writer.println("            </tbody>");
            writer.println("        </table>");

            writer.println("    </div>");
            // End of Document
            writer.println("    <div class='footer'>End of Report - FlipSide Network Log Analysis System</div>");
            writer.println("</body>");
            writer.println("</html>");

            System.out.println("Report successfully exported to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
}
