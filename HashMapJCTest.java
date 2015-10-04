package kpcb;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

/**
 * JUnit tests for the class HashMapJC
 * junit.jar and hamcrest.core files included in the package.
 * 
 * @author Joon Hyuck Choi
 *
 */
public class HashMapJCTest {
	
	private HashMapJC<Integer> map;

	@Before
	public void makeMap() {
		map = new HashMapJC<>(10);
	}

	//Test constructor, isEmpty()
	@Test
	public void testNewEmpty() {
		assertEquals(true, map.isEmpty());
	}

	//Test set(), isEmpty()
	@Test
	public void testSetNotEmpty() {
 		map.set("first", 1);
 		assertEquals(false, map.isEmpty());
	}

	//Test set(), delete(), isEmpty()
	@Test
	public void testSetDeleteEmpty() {
 		map.set("first", 1);
 		map.delete("first");
		assertEquals(true, map.isEmpty());
	}

	//Test set(), get()
	@Test
	public void testSetGetEqual() {
 		map.set("first", 1);
		assertEquals(1, (int) map.get("first"));
		
		map.set("second", 2);
		assertEquals(2, (int) map.get("second"));
		
		map.set("third", 3);
		assertEquals(3, (int) map.get("third"));
	}

	//Test get(), delete() in case when value matching the key does not exist
	@Test
	public void testNotContains() {
		map.set("second", 2);
		assertEquals(null, map.get("first"));
		assertEquals(null, map.delete("first"));
	}
	
	//Test set(), contains(), delete() in case when same key is used multiple tiems
	@Test
	public void testSameKey() {
		assertEquals(false, map.contains("first"));
		map.set("first", 1);
		assertEquals(new Integer(1), (Integer) map.get("first"));
		
		assertEquals(true, map.contains("first"));
		map.set("first", 11);
		assertEquals(new Integer(1), (Integer) map.get("first"));
		
		map.set("first", 111);
		assertEquals(new Integer(1), (Integer) map.get("first"));
		
		assertEquals(3, map.getNumElements());
		
		map.set("second", 2);
		assertEquals(4, map.getNumElements());
		
		assertEquals(new Integer(2), (Integer) map.get("second"));
		assertEquals(new Integer(1), (Integer) map.get("first"));
		
		map.delete("second");
		assertEquals(3, map.getNumElements());
		assertEquals(new Integer(1), (Integer) map.get("first"));
		assertEquals(false, map.get("first") == 111);
		
		assertEquals(true, map.contains("first"));
		map.delete("first");
		assertEquals(new Integer(11), (Integer) map.get("first"));
		
		assertEquals(true, map.contains("first"));
		map.delete("first");
		assertEquals(new Integer(111), (Integer) map.get("first"));
		
		assertEquals(true, map.contains("first"));
		map.delete("first");
		assertEquals(false, map.contains("first"));
		
		assertEquals(true, map.isEmpty());
 	}
	
	//Test set(), isFull()
	@Test
	public void testFull() {
		//also tests if linear probing is working fine.
		map.set("first", 1);
		assertEquals(false, map.isFull());
		map.set("first", 1);
		map.set("first", 1);
		assertEquals(false, map.isFull());
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		assertEquals(false, map.isFull());
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		assertEquals(true, map.isFull());
	}

	//Test set(), load()
	@Test
	public void testLoad() {
		assertEquals(0.0, map.load(), 0.001);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		assertEquals(0.5, map.load(), 0.001);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		assertEquals(1.0, map.load(), 0.001);
	}
	
	//Test delete() on empty hashmap
 	@Test
 	public void testDeleteEmpty() {
 		assertEquals(null, map.delete("first"));
 	}
 	
 	//Test get() on empty hashmap
 	@Test
 	public void testGetEmpty() {
 		assertEquals(null, map.get("first"));
 	}
 	
 	//Test the return value of set()
 	@Test
 	public void testSetLimit() {
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		map.set("first", 1);
		assertEquals(true, map.set("first", 1));
		assertEquals(false, map.set("first", 1));
 	}

}