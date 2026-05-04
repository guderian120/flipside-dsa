package javaCode;

import java.util.List;

/**
 * Generic Interface for all Linked List implementations in the system.
 * This defines the 'Contract' that all list types must follow, 
 * enabling Polymorphism throughout the application.
 */
public interface LinkedList<T> {
    
    /**
     * Adds an item of type T to the list.
     */
    void add(T data);

    /**
     * Removes the first occurrence of the specified item.
     */
    void remove(T data);

    /**
     * Checks if the item exists in the list.
     */
    boolean contains(T data);

    /**
     * Returns the current number of nodes in the list.
     */
    int size();

    /**
     * Deletes all nodes and resets the list.
     */
    void clear();

    /**
     * Converts the custom linked list into a standard java.util.List
     * for easier data analysis and iteration.
     */
    List<T> toList();
}
