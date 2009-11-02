/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.function.TimeFunction;

/**
 * A structured loop flow which resembles a for loop. The internal flow is
 * executed n times. n is defined by using the <code>iterations</code> attribute.
 * Be careful when using a continuous random distribution for defining the number
 * of iterations, since decimal values are rounded to the closest integer.  
 * @author Iván Castilla Rodríguez
 */
public interface ForLoopFlow extends StructuredLoopFlow {
	/** Loop iterations */
	TimeFunction getIterations(); 
}
