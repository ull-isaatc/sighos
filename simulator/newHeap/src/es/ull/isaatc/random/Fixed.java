/*
 * Fixed.java
 *
 * Created on 3 de septiembre de 2004, 11:53
 */

package es.ull.isaatc.random;

/**
 * A generator that always returns the same value.
 * @author Iván Castilla Rodríguez
 */
public class Fixed extends RandomNumber {
	/** The value that this generator always returns. */
    double value;
    
    /**
     * Creates a new fixed random number generator initialized with a double value.
     * @param val The value that this generator always returns. 
     */
    public Fixed(double val) {
        value = val;
    }
    
    /**
     * Creates a new fixed random number generator initialized with an int value.
     * @param val The value that this generator always returns. 
     */
    public Fixed(int val) {
        value = (double) val;
    }

    @Override
    public double sampleDouble() {
        return value;
    }
    
    @Override
    public int sampleInt() {
        return (int) value;
    }     
}
