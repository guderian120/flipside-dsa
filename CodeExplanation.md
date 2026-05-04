# Network Log Analysis System - Code Explanation

This document provides a detailed explanation of the classes and methods within the Java implementation of the Network Log Analysis System.

## 1. Interface: `LinkedList<T>`
Defines the standard operations for any linked list implementation used in this project.

*   `void add(T data)`: Appends an element to the list.
*   `void remove(T data)`: Removes a specific element from the list.
*   `boolean contains(T data)`: Checks if an element exists in the list.
*   `int size()`: Returns the number of elements in the list.
*   `void clear()`: Removes all elements from the list.
*   `List<T> toList()`: Converts the custom list to a standard Java `ArrayList`.

---

## 2. Implementation: `SinglyLinkedList<T>`
A basic linked list where each node points only to the next node.

*   **`Node<T>` (Internal Class)**: Contains `data` and a `next` pointer.
*   **`add(T data)`**: 
    *   Creates a new node.
    *   If the list is empty (`head == null`), sets the new node as the head.
    *   Otherwise, traverses from the head to the last node (where `next` is null) and attaches the new node there.
*   **`remove(T data)`**: 
    *   If the head matches the data, shifts the head to the next node.
    *   Otherwise, searches for a node whose `next` node matches the data, then "skips" that node by linking to `current.next.next`.
*   **`toList()`**: Iterates through nodes starting from `head`, adding each data element to an `ArrayList`.

---

## 3. Implementation: `DoublyLinkedList<T>`
A bidirectional list where each node points to both the next and the previous node.

*   **`Node<T>` (Internal Class)**: Contains `data`, `next`, and `prev` pointers.
*   **`add(T data)`**: 
    *   Uses a `tail` pointer for $O(1)$ insertion.
    *   Connects `tail.next` to the new node and the new node's `prev` to the current `tail`.
    *   Updates the `tail` to the new node.
*   **`remove(T data)`**: 
    *   Finds the node to remove.
    *   Updates the `next` of the previous node and the `prev` of the next node to point to each other, effectively removing the target node from the chain.
*   **`toList()`**: Traverses from `head` to `tail` using `next` pointers.

---

## 4. Implementation: `CircularLinkedList<T>`
A list where the last node points back to the first node, forming a circle.

*   **`add(T data)`**:
    *   Connects the new node after the current `tail`.
    *   Sets the new node's `next` to point back to the `head`.
    *   Updates the `tail` pointer.
*   **`remove(T data)`**:
    *   Uses a `do-while` loop to traverse the circle.
    *   Correctly handles the case where the head or tail is removed by updating the circular link.
*   **`toList()`**: Traverses starting from `head` and stops once it returns to the `head`.

---

## 5. Model: `LogEntry`
Represents a network log entry and handles string parsing.

*   **`LogEntry(String logLine)` (Constructor)**: 
    *   Uses **Regular Expressions (Regex)** to extract timestamp, hostname, process, PID, and message from a raw log string.
    *   Automatically assigns a **Severity** level (4 for "Failed password", 1 for "Accepted password").
*   **Getters**: Provide access to log metadata (IP, User, Severity, etc.).
*   **`toString()`**: Formats the log for readable console output.

---

## 6. Controller: `NetworkLogManager`
The primary class for managing and analyzing logs.

*   **`addLog(LogEntry log)`**: Adds a log to the internal list. If using a `Circular` list with a `maxSize`, it automatically removes the oldest log when the limit is reached.
*   **`detectSuspiciousActivity()`**: 
    *   Uses Java Streams to group failed logins by IP.
    *   Flags IPs with more than 3 failed attempts or high-frequency traffic (>20 logs).
*   **`switchListType(String newType)`**: 
    *   Converts current logs to a temporary list.
    *   Re-initializes the internal `logList` with a new implementation (Singly, Doubly, or Circular).
    *   Re-inserts all logs into the new structure.
*   **`readLogsFromFile(String filePath)`**: Uses `BufferedReader` to read a text file line-by-line and create `LogEntry` objects.
*   **`removeExpiredLogs(int minutesOld)`**: Filters and removes logs whose timestamp is older than the specified duration.
*   **`traverseForward/Backward()`**: Demonstrates the traversal capabilities of the underlying list.

---

## 7. Driver: `project`
The entry point of the application.

*   **`main(String[] args)`**: 
    *   Simulates a real-world scenario by reading logs from a file.
    *   Generates 50 random logs to test scaling.
    *   Demonstrates switching between Singly, Doubly, and Circular list types.
    *   Triggers the "Circular Buffer" rotation to show how the system handles memory constraints.
*   **`generateRandomLog()`**: A utility to create realistic-looking log data for testing.
