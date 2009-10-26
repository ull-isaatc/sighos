/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.model.Model;

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
	 * @param model Model this flow belongs to.
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
	 * @param model Model this flow belongs to.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Model model, TaskFlow subFlow, TimeFunction iterations) {
		this(model, subFlow, subFlow, iterations);
	}
	
}
