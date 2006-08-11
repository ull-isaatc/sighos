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
	int compareTo(Orderable o);
}
