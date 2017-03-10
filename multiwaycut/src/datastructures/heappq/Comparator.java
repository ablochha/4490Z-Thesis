package datastructures.heappq;

/**
 * Comparator.java
 * 
 * Course: CS4445
 * Author: Andrew Bloch-Hansen
 * 
 * Strategy design pattern for comparing objects.
 */
public interface Comparator {
  
    //compare returns negative integer if a < b, 0 if a = b, and positive integer if a > b 
    public int compare(Object a, Object b) throws ClassCastException;  
    
} //end Comparator
