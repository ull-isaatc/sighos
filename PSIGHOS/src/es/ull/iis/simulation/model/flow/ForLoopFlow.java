/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunction;

/**
 * A structured loop flow which resembles a for loop. The internal flow is
 * executed n times. n is defined by using the <code>iterations</code> attribute.
 * Be careful when using a continuous random distribution for defining the number
 * of iterations, since decimal values are rounded to the closest integer.  
 * @author Iván Castilla Rodríguez
 */
public class ForLoopFlow extends StructuredLoopFlow {
	/** Loop iterations */
	protected final TimeFunction iterations; 

	/**
	 * Create a new ForLoopFlow.
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, TimeFunction iterations) {
		super(initialSubFlow, finalSubFlow);
		this.iterations = iterations;
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(TaskFlow subFlow, TimeFunction iterations) {
		this(subFlow, subFlow, iterations);
	}
	
	/**
	 * Returns the function which characterizes the iterations performed in the loop.
	 * @return The function which characterizes the iterations performed in the loop
	 */
	public TimeFunction getIterations() {
		return iterations;
	}

}
