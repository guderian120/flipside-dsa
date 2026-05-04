package javaCode;

/**
 * A Circular Linked List implementation.
 * The tail node's next pointer points back to the head.
 * This is ideal for buffer rotation and continuous data streams.
 */
public class CircularLinkedList<T> implements LinkedList<T> {
    
    /**
     * Internal Node for Circular List.
     */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    /**
     * Adds a new node to the list.
     * Complexity: O(1) using the tail pointer.
     */
    @Override
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            // First item: points to itself
            head = tail = newNode;
            tail.next = head; 
        } else {
            // Add to the end and reconnect to head
            tail.next = newNode;
            tail = newNode;
            tail.next = head; 
        }
        size++;
    }

    /**
     * Removes an item and maintains the circle.
     * Complexity: O(n)
     */
    @Override
    public void remove(T data) {
        if (head == null)
            return;

        Node<T> current = head;
        Node<T> prev = tail; // Previous of head is always tail in a circular list

        // Use a do-while because we need to check the head node too
        do {
            if (current.data.equals(data)) {
                if (size == 1) {
                    head = tail = null;
                } else {
                    // Update pointers to bypass the removed node
                    prev.next = current.next;
                    if (current == head) {
                        head = head.next;
                        tail.next = head;
                    } else if (current == tail) {
                        tail = prev;
                    }
                }
                size--;
                return;
            }
            prev = current;
            current = current.next;
        } while (current != head);
    }

    @Override
    public boolean contains(T data) {
        if (head == null)
            return false;

        Node<T> current = head;
        do {
            if (current.data.equals(data))
                return true;
            current = current.next;
        } while (current != head);
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
     * Iterates once through the circle and returns an ArrayList.
     */
    @Override
    public java.util.List<T> toList() {
        java.util.List<T> list = new java.util.ArrayList<>();
        if (head == null)
            return list;

        Node<T> current = head;
        do {
            list.add(current.data);
            current = current.next;
        } while (current != head);
        return list;
    }
}