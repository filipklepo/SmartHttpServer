package hr.fer.zemris.java.custom.collections;

/**
 * The ObjectStack class is an Adaptor element in the Adapter design pattern. 
 * Purpose of ObjectStack is to provide to user methods which are natural 
 * for a stack and hide everything else. ObjectStack contains inner collection in
 * which elements are actually stored, but with the proper invoking of collection's
 * methods ObjectStack actually acts like a stack. 
 * 
 * @author Filip Klepo
 *
 */
public class ObjectStack {
	
	/**
	 * Private instance of ArrayIndexedCollection.
	 */
	private ArrayIndexedCollection collection;
	
	/**
	 * The default constructor.
	 */
	public ObjectStack() {
		collection = new ArrayIndexedCollection();
	}
	
	/**
	 * Checks if stack is empty.
	 * 
	 * @return <b>'true'</b> if stack is empty
	 */
	public boolean isEmpty(){
		return collection.isEmpty();
	}
	
	/**
	 * Gets size of stack.
	 * 
	 * @return size of stack
	 */
	public int size() {
		return collection.size();
	}
	
	/**
	 * Pushes given value on stack. Null value must not be placed on stack.
	 * 
	 * @param value Object to be pushed on stack
	 */
	public void push(Object value) {
		collection.add(value);
	}
	
	/**
	 * Removes last value pushed on stack from stack and returns it.
	 * 
	 * @return instance of the element deleted from stack
	 * @throws EmptyStackException if collection is empty
	 */
	public Object pop() {
		if(collection.size() == 0) {
			throw new EmptyStackException();
		}
		
		Object lastElement = collection.get(collection.size() - 1);
		collection.remove(collection.size() - 1);
		
		return lastElement;
	}
	
	/**
	 * Returns last element placed on stack but does not delete it from stack. 
	 * 
	 * @throws EmptyStackException if stack is empty
	 * @return instance of last element of stack
	 */
	public Object peek() {
		if(collection.size() == 0) {
			throw new EmptyStackException();
		}
		
		return collection.get(collection.size() - 1);
	}
	
	/**
	 * Clears the stack.
	 */
	public void clear() {
		collection.clear();
	}

}