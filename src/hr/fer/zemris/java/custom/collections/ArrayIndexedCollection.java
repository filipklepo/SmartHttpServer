package hr.fer.zemris.java.custom.collections;

/**
 * ArrayIndexedCollection is implementation of resizable array-backed collection
 * of objects which extends class Collection. General contract of this collection is: 
 * duplicate elements are allowed; storage of null references is not allowed.
 * 
 * @author Filip Klepo
 *
 */
public class ArrayIndexedCollection extends Collection {
	
	/**
	 * Current size of collection (number of elements actually stored). 
	 */
	private int size;
	/**
	 * Current capacity of allocated array of object references.
	 */
	private int capacity;
	/**
	 * An array of object references whose length is determined by <b>capacity</b>.
	 */
	private Object[] elements;
	
	/**
	 * The default constructor which delegates construction to more complex constructor with
	 * initial capacity set to 16.
	 */
	public ArrayIndexedCollection() {
		this(16, null);
	}
	
	/**
	 * Delegates construction to more complex constructor with given initial capacity.
	 * 
	 * @param initialCapacity initial capacity of collection
	 */
	public ArrayIndexedCollection(int initialCapacity) {
		this(initialCapacity, null);
	}
	
	/**
	 * Delegates construction to more complex constructor with initial capacity set to 16 and
	 * given collection.
	 * 
	 * @param other Collection whose elements are copied into this collection
	 */
	public ArrayIndexedCollection(Collection other) {
		this(16, other);
	}
	
	/**
	 * Constructs instance of this class with given initial capacity and given Collection. 
	 * Adds elements from given Collection into this collection.
	 * 
	 * @param initialCapacity initial capacity of collection
	 * @param other Collection whose elements are copied into this collection
	 * @throws IllegalArgumentException if given initial capacity is less than 1 
	 */
	public ArrayIndexedCollection(int initialCapacity, Collection other) {
		if(initialCapacity < 1) {
			throw new IllegalArgumentException("Initial capacity must be greater than or equal to 1!");
		}
		
		capacity = initialCapacity;
		elements = new Object[initialCapacity];
		
		if(other != null) {
			addAll(other);
		}
	}
	
	/**
	 * Doubles up capacity of this collection.
	 */
	private void doubleUpCapacity() {
		capacity *= 2;
		Object[] newElementsArray = new Object[capacity];
		
		for(int i = 0; i < size; ++i) {
			newElementsArray[i] = elements[i];
		}
		
		elements = newElementsArray;
	}
	
	/**
	 * Adds Object into this collection. If collection is full before adding the element, 
	 * collection is reallocated by doubling its capacity.
	 * 
	 * @param value Object which is added to collection
	 * @throws IllegalArgumentException if given value is null
	 */
	@Override
	public void add(Object value) {
		if(value == null) {
			throw new IllegalArgumentException("Storage of null elements in not allowed!");
		}
		
		if(size == capacity) {
			doubleUpCapacity();
		}
		
		elements[size] = value;
		++size;
	}
	
	/**
	 * Gets element stored at given index in collection.
	 * 
	 * @param index index in which searched element is possibly placed
	 * @throws IndexOutOfBoundsException if index is not in 0 to (size - 1) range
	 * @return Object reference to searched element
	 */
	public Object get(int index) {
		if(index < 0 || index > (size - 1)) {
			throw new IndexOutOfBoundsException("Valid indexes are 0 to " + (size-1) + ".");
		}
		
		return elements[index];
	}
	
	@Override
	public void clear() {	
		for(int i = 0; i < size; ++i) {
			elements[i] = null;
		}
		
		size = 0;
	}
	
	/**
	 * Inserts (does not overwrite) given value at the given position.
	 * 
	 * @param value value of new element
	 * @param position position in which new element will possibly be placed
	 * @throws IllegalArgumentException if null element is given as parameter
	 * @throws IndexOutOfBoundsException if position is not in 0 to size interval
	 */
	public void insert(Object value, int position) {
		if(value == null) {
			throw new IllegalArgumentException("Collection does not accept null values!");
		}
		
		if(position < 0 || position > size) {
			throw new IndexOutOfBoundsException("Valid indexes are 0 to " + size + ".");
		}
		
		if(size == capacity) {
			doubleUpCapacity();
		}
		
		if(position == size) {
			add(value);
		} else {
			//shift elements at greater positions one place toward the end
			for(int i = size; i > position; --i) {
				elements[i] = elements[i-1];
			}
			
			elements[position] = value;
			++size;
		}
	}
	
	/**
	 * Gets the index of the first occurrence of the given value or -1 if the value is not found. 
	 * 
	 * @param value value whose index is searched
	 * @return index of given element, <b>-1</b> is returned if element is not in
	 * collection
	 */
	public int indexOf(Object value) {
		if(value == null) {
			return -1;
		}
		
		for(int i = 0; i < size; ++i) {
			if(elements[i].equals(value)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Removes element at specified index in collection.
	 * 
	 * @param index of element which will be removed
	 * @throws IndexOutOfBoundsException if index is not in 0 to (size - 1) range 
	 */
	public void remove(int index) {
		if(index < 0 || index > (size - 1)) {
			throw new IndexOutOfBoundsException("Valid indexes are 0 to " + (size-1) + ".");
		}
		
		for(int i = index; i < size; ++i) {
			elements[i] = elements[i+1];
		}
		
		--size;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean contains(Object value) {
		if(indexOf(value) != -1) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean remove(Object value) {
		if(!contains(value)) {
			return false;
		}
		
		remove(indexOf(value));
		
		return true;
	}

	@Override
	public Object[] toArray() {
		//Java allows creating arrays whose size is 0!
		Object[] array = new Object[size];
		
		for(int i = 0; i < size; ++i) {
			array[i] = elements[i];
		}
		
		return array;
	}

	@Override
	public void forEach(Processor processor) {
		if(size == 0) {
			return;
		}
		
		for(int i = 0; i < size; ++i) {
			processor.process(elements[i]);
		}
	}
	
}
