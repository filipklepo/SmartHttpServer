package hr.fer.zemris.java.custom.collections;

/**
 * The Collection class contains basic methods which are used for working with
 * some general collection of objects. Operations that one performs on data such as 
 * searching, insertion, manipulation, deletion etc. can be performed by this class.
 * 
 * @author Filip Klepo
 *
 */
public class Collection {
	
	/**
	 * Default protected constructor.
	 */
	protected Collection() {
		
	}
	
	/**
	 * Checks if collection is empty.
	 * 
	 * @return <b>'true'</b> if collection contains no objects
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Gets size of collection.
	 * 
	 * @return number of currently stored objects in this collection
	 */
	public int size() {
		return 0;
	}
	
	/**
	 * Adds the given object into this collection. 
	 * 
	 * @param value Object which is added to collection
	 */
	public void add(Object value) {
		
	}
	
	/**
	 * Checks if collection contains given element.
	 * 
	 * @param value value whose presence is checked 
	 * @return <b>'true'</b> if collection contains given value
	 */
	public boolean contains(Object value) {
		return false;
	}
	
	/**
	 * Removes given element from collection if it is present in collection.
	 * 
	 * @param value reference to element which will be removed from collection
	 * @return <b>'true'</b> if element is removed
	 */
	public boolean remove(Object value) {
		return false;
	}
	
	/**
	 * Gets Object array filled with collection elements. This method never returns null.
	 * 
	 * @throws UnsupportedOperationException if method is not implemented 
	 * @return new Object array filled with collection elements 
	 */
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Performs action defined by Processor on elements of collection.
	 * 
	 * @param processor Processor with defined behavior
	 */
	public void forEach(Processor processor) {
		
	}
	
	/**
	 * Adds into itself all elements from given collection. This other collection 
	 * remains unchanged. 
	 * 
	 * @param other Collection or its derivative whose elements are added to
	 * this collection 
	 */
	public void addAll(Collection other) {
		
		/**
		 * Local class which extends processor. Task of this class is to add to this
		 * collection every element given as parameter to process method.
		 * 
		 * @author Filip Klepo
		 *
		 */
		class LocalProcessor extends Processor {

			@Override
			public void process(Object value) {
				Collection.this.add(value);
			}
			
		}

		other.forEach(new LocalProcessor());
	}
	
	/**
	 * Removes all elements from this collection. 
	 */
	public void clear() {
		
	}

}
