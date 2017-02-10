/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link StructuredLoopFlow} which resembles a for loop. The internal flow is
 * executed n times. Be careful when using a continuous random distribution for 
 * defining the number of iterations, since decimal values are rounded to the closest integer.  
 * @author Iván Castilla Rodríguez
 */
public interface ForLoopFlow<WT extends WorkThread<?>> extends StructuredLoopFlow<WT> {
	/**
	 * Returns the function which characterizes the iterations performed in the loop.
	 * @return The function which characterizes the iterations performed in the loop
	 */
	TimeFunction getIterations(); 
}
