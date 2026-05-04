package javaCode;

/**
 * A standard Doubly Linked List implementation.
 * Each node points to both the next and previous nodes, allowing
 * for bidirectional traversal and O(1) removals if the node is known.
 */
public class DoublyLinkedList<T> implements LinkedList<T> {
    
    /**
     * Internal Node class for Doubly Linked List.
     * Contains two pointers: next and prev.
     */
    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head; // First element
    private Node<T> tail; // Last element (allows O(1) addition)
    private int size;    // Cache the size for O(1) access

    /**
     * Adds an element to the end of the list.
     * Complexity: O(1) because we maintain a tail pointer.
     */
    @Override
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            // First item in the list
            head = tail = newNode;
        } else {
            // Link the current tail to the new node
            tail.next = newNode;
            newNode.prev = tail;
            // Move the tail pointer to the new end
            tail = newNode;
        }
        size++;
    }

    /**
     * Removes the first occurrence of the specified data.
     * Complexity: O(n) as we must search for the value first.
     */
    @Override
    public void remove(T data) {
        if (head == null)
            return;

        Node<T> current = head;
        // Search for the node containing the data
        while (current != null && !current.data.equals(data)) {
            current = current.next;
        }

        // If the node was found, perform the deletion by updating neighbors
        if (current != null) {
            if (current == head) {
                // Deleting the head
                head = head.next;
                if (head != null)
                    head.prev = null;
                else
                    tail = null;
            } else if (current == tail) {
                // Deleting the tail
                tail = tail.prev;
                tail.next = null;
            } else {
                // Deleting a middle node (Standard O(1) splice)
                current.prev.next = current.next;
                current.next.prev = current.prev;
            }
            size--;
        }
    }

    @Override
    public boolean contains(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data))
                return true;
            current = current.next;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        head = tail = null;
        size = 0;
    }

    /**
     * Converts the internal structure to a standard Java ArrayList.
     */
    @Override
    public java.util.List<T> toList() {
        java.util.List<T> list = new java.util.ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}