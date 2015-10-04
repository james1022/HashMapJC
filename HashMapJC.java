package kpcb;

/**
 * This is a HashMap implementation using linear probing for 
 * hashcode collisions.
 * 
 * No rehash() method was implemented because the 
 * problem statement asked the HashMap to be of a fixed size.
 * 
 * Test code in file HashMapJCTest.java in JUnit testing.
 * The test file resides in the same package as this file.
 * Necessary .jar files for JUnit included in the project.
 * 
 * @author Joon Hyuck Choi
 *
 * @param <T> Generic type for HashMapJC class.
 */
public class HashMapJC<T> {

	/** The capacity of the hashmap. */
	private int capacity;
	
	/** The number of elements currenlty in the hashmap. */
	private int numElements;
	
	/** The hashmap of values itself. */
	private T[] values;
	
	/** For each of the values, the corresponding String key.
	 *  This exists because I allowed users to use the same key multiple times. */
	private String[] keys;
	
	/** Indicates whether the hashmap value at the given index is a zombie value or not.
	 *  Only has significance when the hashmap value at the given index is null.
	 *  
	 *  Case 1: hashmap value == null && isZombie
	 *  Then we see if anything previously existed in the slot and then was deleted.
	 *  If that was the case, the slot is marked as a "zombie" (i.e. a zombie of the
	 *  value that existed here previously is still sitting here.) This allows for
	 *  correct find() operations using linear probing. (Again, considering the fact
	 *  that users might use the same key multiple times) 
	 *  
	 *  Case 2: hashmap value == null && !isZombie
	 *  In this case, we know that this null slot in the hashmap had never been
	 *  occupied by an element before. So, when we hit this slot while searching
	 *  for a value with a given key in the hashmap (in the get(), contains(), find()
	 *  methods in my code), we are safe to stop searching and report that the
	 *  element does not exist in the map. */
	private boolean[] zombieIndicator;
	
	/**
	 * Constructor for the HashMapJC class.
	 * @param size the initial capacity of the HashMap.
	 */
	@SuppressWarnings("unchecked")
	public HashMapJC(int size) {
		this.numElements = 0;
		this.capacity = size;
		this.values = (T[]) new Object[this.capacity];
		this.keys = new String[size];
		this.zombieIndicator = new boolean[size];
	}
	
	/**
	 * Sets the value at the appropriate spot in the hashmap.
	 * Allows use of the same key multiple times.
	 * 
	 * @param key String key for a specific value.
	 * @param value the value to be stored in the hashmap.
	 * @return true if succeeded in setting the value. False if else.
	 * 		   The only case in which the return value can be false
	 * 		   is when the hashmap is full. (Requires "rehash()"-ing.)
	 */
	public boolean set(String key, T value) {
		//First check if there is any room left.
		if (this.isFull()) {
			return false;
		}
		
		//Calculate the hashcode.
		int code = Math.abs(key.hashCode()) % this.capacity;
		
		//Probe for an empty spot.
		while (this.values[code] != null) {
			//Wrap around when we have reached the end of the array.
			if (code == this.capacity - 1) {
				code = -1;
			}
			code++;
		}
		
		//Store the value in the map.
		this.values[code] = value;
		this.keys[code] = key;
		this.zombieIndicator[code] = false;
		this.numElements++;
		return true;
		
	}
	
	/**
	 * Returns the value associated with the given key.
	 * If multiple values exist with the given key, the value
	 * closest to the hashcode calculated directly from "key"
	 * will be returned. If no value exists at that spot,
	 * we use linear probing to search for a value with the
	 * given key.
	 * 
	 * @param key the key of the value to be returned.
	 * @return the value if it exists, null if not.
	 */
	public T get(String key) {
		if (this.contains(key)) {
			//If the map contains the key, return the  value associated with it.
			return this.values[this.find(key)];
		} else {
			return null;
		}
	}
	
	/**
	 * Deletes a value associated with the given key.
	 * If multiple values exist with the given key, the value
	 * closest to the hashcode calculated directly from "key"
	 * will be deleted. If no value exists at that spot,
	 * we use linear probing to search for a value with the
	 * given key.
	 * 
	 * @param key the key of the value to be deleted.
	 * @return the value that was deleted if found, null if else.
	 */
	public T delete(String key) {
		if (this.contains(key)) {
			int code = this.find(key);
			T result = this.values[code];
			this.values[code] = null;
			this.keys[code] = null;
			this.zombieIndicator[code] = true;
			this.numElements--;
			return result;
		} else {
			return null;
		}
	}
	
	/**
	 * Calculates the load factor of the hashmap.
	 * Cannot be greater than 1.
	 * 
	 * @return the load factor of the hashmap.
	 */
	public float load() {
		return ((float) this.numElements) / this.capacity;
	}
	
	/**
	 * Checks to see if a value corresponding to the given key exists.
	 * Makes use of the "zombie" concept to search the map
	 * using linear probing. 
	 * 
	 * @param key key of the value being searched for.
	 * @return true if found, false if not.
	 */
	protected boolean contains(String key) {
		//First checks if the map is empty.
		if (this.isEmpty()) {
			return false;
		}
		
		//Calculates the default hash code for the given key.
		int code = Math.abs(key.hashCode()) % this.capacity;
		
		//Count how many times we've looped in the while loop below.
		//To be used as a condition to break out of the while loop in a special
		//case described below.
		int loopCount = 0;
		
		
		//If the slot at index 'code' is not null OR is a zombie slot (i.e. something USED to be there),
		//keep on looking. Don't jump out of the loop.
		while (this.values[code] != null || this.isZombie(code)) {
			//If the map at this index is not null, check if the given key matches
			//with the key associated with the value in the map at index 'code'.
			if (this.values[code] != null) {
				if (key.equals(this.keys[code])) {
					return true;
				}
			}

			//Wrap around when we've reached the end of the array.
			if (code == this.capacity - 1) {
				code = -1;
			}
			//If the keys don't match, keep probing until we hit a slot that is
			//both null-valued and is not a zombie spot (Nothing was never there at that index before).
			code++;
			
			//Prevent an infinite loop in the case where:
			//1. the map is currently full, 
			//2. there is no match in the map for the given key, and
			//3. there is no truly empty slot (i.e. is-null && non-zombie) that 
			//we can use as a condition to break out of this while loop.
			loopCount++;
			if (loopCount > this.capacity) {
				break;
			}
		}
		
		return false;
	}
	
	/**
	 * Finds the index of the object given its key.
	 * 
	 * When we call this method in the get(key) method,
	 * we know the object exists because this method will only get called
	 * after we've confirmed the existence with a contains(key) method call.
	 * 
	 * Searches for the value starting at the position that it is expected to be at.
	 * It might not exactly be there because there might have been
	 * a hashcode collision and thus the value was placed somewhere else.
	 * So we probe through the map. (Linear Probing)
	 * 
	 * @param key the key of the object to be searched for.
	 * @return the index of the map array under which the object actually lies.
	 */
	protected int find(String key) {
		//Calculates the default hash code for the given key.
		int code = Math.abs(key.hashCode()) % this.capacity;	
		int result = -1;
		
		
		//If the slot at index 'code' is not null OR is a zombie slot (i.e. something USED to be there),
		//keep on looking. Don't jump out of the loop.
		while (this.values[code] != null || this.isZombie(code)) {
			//If the map at this index is not null, check if the given key matches
			//with the key associated with the value in the map at index 'code'.
			if (this.values[code] != null) {
				if (key.equals(this.keys[code])) {
					result = code;
					break;
				}
			}
			
			//Wrap around when we've reached the end of the array.
			if (code == this.capacity - 1) {
				code = -1;
			}
			//If the keys don't match, keep probing until we hit a slot that is
			//both null-valued and is not a zombie spot (Nothing was never there at that index before).
			code++;
		}
		return result;
	}
	
	/**
	 * The slot to be examined has to be null-valued.
	 * (Because non-null-valued slots are always non-zombies.)
	 * 
	 * Returns true if the slot at the given index "code" is a zombie.
	 * This means that something used to exist in the slot before,
	 * but it was deleted in the past.
	 * 
	 * False if the slot is both null and not zombie.
	 * This means that no element had never ever existed in that 
	 * slot before.
	 * 
	 * @param code the index at which the hashmap array is to be examined for "zombiness"
	 * @return true if the slot is a zombie, false if not.
	 */
	protected boolean isZombie(int code) {
		return this.zombieIndicator[code];
	}
	
	/**
	 * Checks if the hashmap is empty.
	 * @return true if empty, false if else.
	 */
	protected boolean isEmpty() {
		return this.numElements == 0;
	}
	
	/**
	 * Checks if the hashmap is full.
	 * @return true if full, false if else.
	 */
	protected boolean isFull() {
		return this.capacity == this.numElements;
	}
	
	/**
	 * Returns the number of elements currently in the hashmap.
	 * 
	 * @return the number of elements currently in the hashmap.
	 */
	public int getNumElements() {
		return this.numElements;
	}
	
}
