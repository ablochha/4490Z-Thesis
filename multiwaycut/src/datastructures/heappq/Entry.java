package datastructures.heappq;

/**
 * HeapPQ.java
 * 
 * Course: CS4445
 * Author: Andrew Bloch-Hansen
 * 
 * Location aware entry for the Vector based heap
 *
 */
public class Entry<Key,Value> implements Position {
	
    private Key _k;			// The key of the entry
    private Value _v;		// The value of the entry
    private int _location;	// The location inside of a data structure
    
    // Constructs a new Entry
    public Entry(Key k, Value v, int loc) {
    	
        _k = k;
        _v = v;
        _location = loc;
        
    } //end Entry
    
    /**
     * Returns the key for the Entry
     * @return the key
     */
    public Key getKey() {
    	
    	return _k;
    	
    } //end getKey
    
    /**
     * Returns the value for the Entry
     * @return the value
     */
    public Value getValue() {
    	
    	return _v;
    	
    } //end getValue
    
    /**
     * Changes the value for the Entry
     * @param newV the new value
     */
    public void changeValue(Value newV) {
    	
    	_v = newV;
    	
    } //end changeValue
    
    /**
     * Changes the key for the Entry
     * @param newK the new key
     */
    public void changeKey(Key newK) {
    	
    	_k = newK;
    	
    } //end changeKey
    
    /**
     * Gets the location for an Entry
     * @return the location
     */
    public int getLocation() {
    	
    	return _location;
    	
    } //end getLocation
    
    /**
     * Sets the location for an Entry
     * @param loc the location
     */
    public void  setLocation(int loc) {
    	
    	_location = loc;
    	
    } //end setLocation
    
    /**
     * Gives the position of the Entry
     * @return the position
     */
    public int givePosition() {
    	
        return(_location);
        
    }; //end givePosition
    
 } //end Entry