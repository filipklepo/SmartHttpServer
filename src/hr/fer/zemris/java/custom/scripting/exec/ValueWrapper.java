package hr.fer.zemris.java.custom.scripting.exec;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * ValueWrapper is a class which wraps a Object. Objects supported by this class are only:
 * null, String, Integer, Double. Besides wrapping the actual object, this class provides 
 * functionality for changing the value of object by incrementing, decrementing,
 * multiplication or dividing it with other objects. NOTE: Numerical operations are not 
 * applicable for String instances. Such attempts will result with exceptions. 
 * 
 * @author Filip Klepo
 *
 */
public class ValueWrapper {

	/**
	 * Wrapped value.
	 */
	private Object value;
	
	/**
	 * Function for incrementing.
	 */
	private final static BiFunction<Double, Double, Double> INCR_BIFUN = 
			(a,b) -> a + b;
	/**
	 * Function for decrementing.
	 */
	private final static BiFunction<Double, Double, Double> DECR_BIFUN = 
			(a,b) -> a - b;
	/**
	 * Function for multiplication.
	 */
	private final static BiFunction<Double, Double, Double> MUL_BIFUN = 
			(a,b) -> a * b;
	/**
	 * Function for dividing.
	 */
	private final static BiFunction<Double, Double, Double> DIV_BIFUN = 
			(a,b) -> a / b;
	/**
	 * Number which indicates how "close" a double number should be close to
	 * zero to be considered a zero.
	 */
	private final static Double ZERO_TRESHOLD = Double.valueOf("1e-10");
	
	/**
	 * Instantiates this class with given value.
	 * 
	 * @param value value of this class
	 * @throws IllegalArgumentException if value is not valid
	 */
	public ValueWrapper(Object value) {
		if(Objects.isNull(value)) {
			this.value = getDoubleValueOfNullObject();
		} else {
			Object doubleValue = transformToDouble(value);
			if(doubleValue == null) {
				throw new IllegalArgumentException(
						"Given value is not a valid number!");
			}
			this.value = doubleValue;
		}
	}

	/**
	 * Increments stored value by given one.
	 * 
	 * @param incValue value for incrementing
	 * @throws IllegalArgumentException if value is not valid
	 * @throws RuntimeException if value could not be decremented for any reason
	 */
	public void increment(Object incValue) {
		if(Objects.isNull(incValue)) {
			incValue = getDoubleValueOfNullObject();
		} else {
			incValue = transformToDouble(incValue);
		}
		if(incValue == null) {
			throw new IllegalArgumentException(
					"Given increment value is not a valid number!");
		}
			
		this.value = calculate(value, incValue, INCR_BIFUN);
	}
	
	/**
	 * Decrements value by given one.
	 * 
	 * @param decValue value for decrementing
	 * @throws IllegalArgumentException if given value is not valid
	 * @throws RuntimeException if value could not be decremented for any reason
	 */
	public void decrement(Object decValue) {
		if(Objects.isNull(decValue)) {
			decValue = getDoubleValueOfNullObject();
		} else {
			decValue = transformToDouble(decValue);
		}
		if(decValue == null) {
			throw new IllegalArgumentException(
					"Given decrement value is not a valid number!");
		}
		
		this.value = calculate(value, decValue, DECR_BIFUN);
	}
	
	/**
	 * Multiplies value with given one.
	 * 
	 * @param mulValue value for multiplication
	 * @throws IllegalArgumentException if given value is not valid
	 * @throws RuntimeException if value could not be multiplied for any reason
	 */
	public void multiply(Object mulValue) {
		if(Objects.isNull(mulValue)) {
			mulValue = getDoubleValueOfNullObject();
		} else {
			mulValue = transformToDouble(mulValue);
		}
		if(mulValue == null) {
			throw new IllegalArgumentException(
					"Given multiplication value is not a valid number!");
		}
		
		this.value = calculate(value, mulValue, MUL_BIFUN);
	}
	
	/**
	 * Divides value by given one.
	 * 
	 * @param divValue value for dividing
	 * @throws IllegalArgumentException if given value is not valid
	 * @throws ArithmeticException if there is attempt of division by zero
	 * @throws RuntimeException if value could not be divided for any reason
	 */
	public void divide(Object divValue) {
		if(Objects.isNull(divValue)) {
			throw new ArithmeticException("Division by zero!");
		} else {
			divValue = transformToDouble(divValue);
		}
		if(divValue == null) {
			throw new IllegalArgumentException(
					"Given division value is not a valid number!");
		}
		
		if(Math.abs((Double)divValue) <= ZERO_TRESHOLD) {
			throw new ArithmeticException("Division by zero!");
		}
		
		this.value = calculate(value, divValue, DIV_BIFUN);
	}
	
	/**
	 * Gets value stored in wrapper.
	 * 
	 * @return value stored in wrapper
	 */
	public Object getValue() {
		Object valueAsInteger = transformToInteger(value);
		if(valueAsInteger == null) {
			return value;
		} else {
			return valueAsInteger;
		}
	}

	/**
	 * Sets new value of wrapper.
	 *  
	 * @param value new value of wrapper
	 * @throws IllegalArgumentException if value is not valid
	 */
	public void setValue(Object value) {
		if(Objects.isNull(value)) {
			this.value = getDoubleValueOfNullObject();
		} else {
			Object doubleValue = transformToDouble(value);
			if(doubleValue == null) {
				throw new IllegalArgumentException(
						"Given value is not a valid number");
			}
			this.value = doubleValue;
		}
	}
	
	/**
	 * Numerically compares two objects. Objects can be numerically compared
	 * if each of them is either Integer or Double. If one of objects is Double,
	 * only his whole number value will be taken for calculation. 
	 * 
	 * @param withValue value for comparison
	 * @throws RuntimeException if one of objects is string
	 * @return integer less than zero if currently stored value is smaller than argument,
	 * an integer greater than zero if currently stored value is larger than argument 
	 * or an integer 0 if they are equal
	 */
	public int numCompare(Object withValue) {
		if(Objects.isNull(withValue)) {
			withValue = getDoubleValueOfNullObject();
		} else {
			withValue = transformToDouble(withValue);
		}
		
		if(value == null) {
			throw new IllegalArgumentException("Given value is not a number!");
		}
		
		return ((Double)calculate(value, withValue, (a,b) -> a - b)).intValue();
	}

	/**
	 * Transforms object to a {@link Double}. If object can not be transformed 
	 * to {@link Double}, null is returned.
	 * 
	 * @param value {@link Object}
	 * @return {@link Double}
	 */
	private Object transformToDouble(Object value) {
		Object number;

		try {
			number = Double.parseDouble(value.toString());
		} catch(NumberFormatException ex) {
			return null;
		}

		return number;
	}
	
	/**
	 * Transforms object to a {@link Integer}. If object can not be transformed 
	 * to {@link Integer}, null is returned.
	 * 
	 * @param value {@link Object}
	 * @return {@link Integer}
	 */
	private Object transformToInteger(Object value) {
		Double doubleValue = (Double)value;
		if(doubleValue%1 != 0) {
			return null;
		}
		
		
		return (Integer)doubleValue.intValue();
	}
	
	/**
	 * Calculates the given function with given operands.
	 * 
	 * @param firstOperand first operand
	 * @param secondOperand second operand
	 * @param fun function
	 * @return result of function
	 */
	private Object calculate(Object firstOperand, Object secondOperand,
			BiFunction<Double, Double, Double> fun) {
		return fun.apply((Double)firstOperand, 
				(Double)secondOperand);
	}

	/**
	 * Gets ValueWrapper's Object representation of null-reference.
	 * 
	 * @return ValueWrapper's Object representation of null-reference
	 */
	private Object getDoubleValueOfNullObject() {
		return Double.valueOf(0);
	}

}
