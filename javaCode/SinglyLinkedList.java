package javaCode;

/**
 * A standard Singly Linked List implementation.
 * Each node only points forward. This is memory efficient but
 * requires O(n) time to add to the tail.
 */
public class SinglyLinkedList<T> implements LinkedList<T> {
    
    /**
     * Internal Node class.
     * Contains data and a reference to the next node only.
     */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head; // Start of the list
    private int size;    // Track number of elements

    /**
     * Adds an element to the tail of the list.
     * Complexity: O(n) because we have to traverse from the head to find the end.
     */
    @Override
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            // Iterate until we find the last node
            while (current.next != null) {
                current = current.next;
            }
            // Link the new node
            current.next = newNode;
        }
        size++;
    }

    /**
     * Removes an element from the list.
     * Complexity: O(n) as we must find the element and the node before it.
     */
    @Override
    public void remove(T data) {
        if (head == null)
            return;

        // Special case: removing the head
        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return;
        }

        Node<T> current = head;
        // Search for the node whose NEXT node contains the data
        while (current.next != null && !current.next.data.equals(data)) {
            current = current.next;
        }

        // Skip the matching node
        if (current.next != null) {
            current.next = current.next.next;
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
        head = null;
        size = 0;
    }

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