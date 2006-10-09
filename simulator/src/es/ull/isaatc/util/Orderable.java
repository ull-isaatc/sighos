/**
 * 
 */
package es.ull.isaatc.util;

/**
 * An "Orderable" object has a key which can be used to compare this
 * object with other "Orderable" objects. The key must be a Comparable 
 * object. <p>
 * The <code>compareTo</code> method of the classes that implements 
 * this interface should compare the keys of both objects.
 * @author Iván Castilla Rodríguez
 */
public interface Orderable extends Comparable {
	/**
	 * Returns the key of this object. 
	 * @return The key of this object.
	 */
	Comparable getKey();
	
	/**
	 * Compares this object with the specified Orderable object for order. Returns a 
	 * negative integer, zero, or a positive integer as this object is less than, equal to, 
	 * or greater than the specified object. 
	 * @param o The Orderable object to be compared
	 * @return a negative integer, zero, or a positive integer as this object is less than, 
	 * equal to, or greater than the specified object.
	 */
	int compareTo(Orderable o);
}
