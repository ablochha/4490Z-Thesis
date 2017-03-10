package datastructures.heappq;

/**
 * HeapPQ.java
 * 
 * Course: CS4445
 * Author: Andrew Bloch-Hansen
 *
 */
import java.util.*;

public class HeapPQ<Key,Value> {
	
    private Vector<Entry<Key,Value>> _heap;	// The vector-based heap
    
    private Comparator _c;					// The comparison strategy

    private int _maxSize;					// The max size
    private int _size;						// The current size of the heap
   
    /**
     * Creates a new heap of size n using comparison strategy cIn
     * @param n size of the heap
     * @param cIn comparison strategy
     */
    public HeapPQ(int n, Comparator cIn) {
    	
    	_heap = new Vector<Entry<Key,Value>>(n+1);
    	_heap.add(new Entry<Key,Value>(null,null,1));
        
        _size = 0;
        _maxSize = n;
        _c       = cIn;
        
    } //end HeapPQ
 
    /**
     * Returns the size of the heap
     * @return the size
     */
    public int size() { 
    		
    	return(_size);
    	
    } //end size
    
    /**
     * Returns if the heap is empty
     * @return true if the heap is empty, false otherwise
     */
    public boolean isEmpty() { 
    	
    	return(_size == 0);
    	
    } //end isEmpty
    
    /**
     * Removes the minimum value of the heap
     * @return the value removed
     * @throws HeapException if the heap is empty
     */
    public Value removeMin() throws HeapException {
    	
    	// Check if the heap is empty
        if (_size == 0 ) {
        	
        	throw new HeapException("Heap is empty");
        	
        } //end if
        
        // Balance the heap
        if (_size > 1) {
        	
        	heapSwap(1, _size);
        	
        } //end if
        
        Value toReturn = _heap.elementAt(_size).getValue();
        
        _heap.remove(_size);
        _size--;
        
        int i = 1;
        int childIndex = findSmallestKeyChild(i);
        
        // Balance the heap
        while ((childIndex != 0) && (_c.compare(_heap.elementAt(childIndex).getKey(), _heap.elementAt(i).getKey()) == -1)) {
        	
            heapSwap(childIndex,i);
            i = childIndex;
            childIndex = findSmallestKeyChild(i);
            
        } //end while
        
        return(toReturn);
        
    } //end removeMin

    // swaps heap entries and their locations
    /**
     * Swaps the heap entries and their locations
     * @param i the first entry
     * @param j the second entry
     */
    private void heapSwap(int i, int j) {
    	
        Entry<Key,Value> temp = _heap.elementAt(i);
        _heap.set(i, _heap.elementAt(j));
        _heap.set(j,temp);
      
        // now update locations of the location-aware entries
        _heap.elementAt(i).setLocation(i); 
        _heap.elementAt(j).setLocation(j); 
        
    } //end heapSwap

    /**
     * Balances the heap
     * @param i the node to balance
     */
    private void upHeap(int i) {

        while (i > 1 && _c.compare(_heap.elementAt(i/2).getKey(), _heap.elementAt(i).getKey()) > 0) {
        	
            heapSwap(i,i/2);
            i = i/2;
            
        } //end while
        
    } //end upHeap
    
    // returns the slot where the location of the item will be held
    /**
     * Returns the the position of an item in the heap after insertion
     * @param k the key to store under
     * @param v the value of the key
     * @return the position in the heap
     * @throws HeapException
     */
    public Position insert(Key k, Value v) throws HeapException {
    	
    	// Check if the size is at the maximum
        if (_size == _maxSize) {
        	
        	throw new HeapException("Heap is full");
        	
        } //end if
        
        _size++;
        
        Entry<Key,Value> entry = new Entry<Key,Value>(k, v, _size);
        _heap.add(entry);
        
        // now perform upheap
        upHeap(_size);
        return ((Position) entry);
        
    } //end insert
    
    /**
     * Change the key at the given position
     * @param p the position in the heap
     * @param newKey the new key
     * @throws HeapException
     */
    public void decreaseKey(Position p, Key newKey) throws HeapException {
        
        Entry e = (Entry) p;
        int heapLoc = e.getLocation();
        
        if (_c.compare(_heap.elementAt(heapLoc).getKey(), newKey) < 0) {
        	
        	throw new HeapException("New key must be smaller in decreaseKey()");
        	
        } //end if
            
        _heap.elementAt(heapLoc).changeKey(newKey);
        upHeap(heapLoc);
        
    } //end decreaseKey
    
    /**
     * Returns an iterator over the values in the heap
     * @return an iterator over the values
     */
    public Iterator<Value> values() {
    	
        LinkedList<Value> l = new LinkedList<Value>();
        
        // Add all the elements to the list
        for ( int i = 1; i <= _size; i++) {
        	
        	l.add(_heap.elementAt(i).getValue());
        	
        } //end for
            
        return l.listIterator();
        
    } //end values
    
    /**
     * Finds the smallest key in the children for balancing
     * @param i the node to check
     * @return the smallest key
     */
    private int findSmallestKeyChild(int i) {
    	
    	// node at index i has 2 children in this case
        if (2*i < _size) { 
        
            if (_c.compare(_heap.elementAt(i*2).getKey(), _heap.elementAt(i*2+1).getKey()) == -1) {
            	
            	return (2*i);
            	
            } //end if
                
            else {
            	
            	return (2*i+1);
            	
            } //end else
            
        } //end if
        
        // node at index i has only a left child, no right child
        else if (2*i == _size) {
        	
        	return (2*i);
        	
        } //end else if
            
        // node at index i is a leaf
        else {
        	
        	return(0); 
        	
        } //end else
        
    } //end findSmallestKeyChild
    
} //end HeapPQ
