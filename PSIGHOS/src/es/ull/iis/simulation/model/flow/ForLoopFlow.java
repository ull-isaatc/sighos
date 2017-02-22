/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.model.Model;

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
	public ForLoopFlow(Model model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, TimeFunction iterations) {
		super(model, initialSubFlow, finalSubFlow);
		this.iterations = iterations;
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Model model, TaskFlow subFlow, TimeFunction iterations) {
		this(model, subFlow, subFlow, iterations);
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Model model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, int iterations) {
		this(model, initialSubFlow, finalSubFlow, new ConstantFunction(iterations));
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Model model, TaskFlow subFlow, int iterations) {
		this(model, subFlow, subFlow, new ConstantFunction(iterations));
	}
	
	/**
	 * Returns the function which characterizes the iterations performed in the loop.
	 * @return The function which characterizes the iterations performed in the loop
	 */
	public TimeFunction getIterations() {
		return iterations;
	}

}
