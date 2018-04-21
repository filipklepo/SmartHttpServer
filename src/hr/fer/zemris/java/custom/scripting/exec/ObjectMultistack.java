package hr.fer.zemris.java.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;

/**
 * ObjectMultistack is a special kind of map, a map which enables users to store
 * multiple values for same key, values which are provided by stack-like abstraction.
 * One can store elements 
 * 
 * @author Filip Klepo
 *
 */
public class ObjectMultistack {
	
	/**
	 * Map which maps names to their stacks.
	 */
	private Map<String, MultistackEntry> map;

	/**
	 * Represents a single entry in stack in ObjectMultiStack. Besides containing actual
	 * value, MultistackEntry holds reference to next element in stack, since stack is implemented as 
	 * a singly-linked list.
	 * 
	 * @author Filip Klepo
	 *
	 */
	private static class MultistackEntry {
		
		/**
		 * Value of entry. 
		 */
		private ValueWrapper value;
		/**
		 * Reference to next element of stack.
		 */
		private MultistackEntry next;
		
		/**
		 * Instantiates MultistackEntry with given value.
		 * 
		 * @param value value which is stored in entry
		 */
		public MultistackEntry(ValueWrapper value) {
			this.value = value;
		}
		
	}
	
	/**
	 * The default constructor.
	 */
	public ObjectMultistack() {
		map = new HashMap<>();
	}
	
	/**
	 * Puts given value on top of stack. Stack is represented by given name.
	 * 
	 * @param name name of stack
	 * @param valueWrapper value placed on top of stack
	 * @throws IllegalArgumentException if given name is not valid
	 */
	public void push(String name, ValueWrapper valueWrapper) {
		if(name == null) {
			throw new IllegalArgumentException("Given name must not be null!");
		}
		//stack is empty, push first element
		if(!map.containsKey(name)) {
			map.put(name, new MultistackEntry(valueWrapper));
			return;
		}
		
		MultistackEntry entry;
		for(entry = map.get(name); entry.next != null; entry = entry.next);
		
		entry.next = new MultistackEntry(valueWrapper);
	}
	
	/**
	 * Removes value from top of stack and returns it. Stack is represented by given name. 
	 * 
	 * @param name name of stack
	 * @return value last value stored on top of stack
	 * @throws IllegalArgumentException if given name is not valid
	 * @throws EmptyStackException if stack with given name is empty
	 */
	public ValueWrapper pop(String name) {
		if(name == null) {
			throw new IllegalArgumentException("Given name must not be null!");
		}
		if(map.get(name) == null) {
			throw new EmptyStackException("Stack is empty.");
		}
		
		MultistackEntry entry = map.get(name);
		ValueWrapper oldValueWrapper;
		
		//stack contains one element
		if(entry.next == null) {
			oldValueWrapper = entry.value;
			map.remove(name);
			return oldValueWrapper;
		}
		
		//iterate until entry is penultimate
		for(; entry.next.next != null; entry = entry.next);
		oldValueWrapper = entry.next.value;
		entry.next = null;
		
		return oldValueWrapper;
	}
	
	/**
	 * Gets value from top of stack without removing it. Stack is represented 
	 * by given name. 
	 * 
	 * @param name name of stack
	 * @return value value stored on top of stack
	 * @throws IllegalArgumentException if given name is not valid
	 * @throws EmptyStackException if stack with given name is empty
	 */
	public ValueWrapper peek(String name) {
		if(name == null) {
			throw new IllegalArgumentException("Given name must not be null!");
		}
		if(map.get(name) == null) {
			throw new EmptyStackException("Stack is empty.");
		}
		
		MultistackEntry entry = map.get(name);
		for(; entry.next != null; entry = entry.next);
		
		return entry.value;
	}
	
	/**
	 * Checks if stack with given name is empty.
	 * 
	 * @param name name of stack
	 * @return <b>'true'</b> if stack is empty
	 */
	public boolean isEmpty(String name) {
		if(name == null) {
			throw new IllegalArgumentException("Given name must not be null!");
		}
		
		return map.get(name) == null;
	}

	
}
