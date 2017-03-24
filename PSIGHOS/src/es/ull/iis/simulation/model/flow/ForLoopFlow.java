/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.SortedMap;
import java.util.TreeMap;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


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
	/** List used by the control system. */
	protected final SortedMap<ElementInstance, Integer> checkList = new TreeMap<ElementInstance, Integer>();

	/**
	 * Create a new ForLoopFlow.
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Simulation model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, TimeFunction iterations) {
		super(model, initialSubFlow, finalSubFlow);
		this.iterations = iterations;
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Simulation model, TaskFlow subFlow, TimeFunction iterations) {
		this(model, subFlow, subFlow, iterations);
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Simulation model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, int iterations) {
		this(model, initialSubFlow, finalSubFlow, new ConstantFunction(iterations));
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Simulation model, TaskFlow subFlow, int iterations) {
		this(model, subFlow, subFlow, new ConstantFunction(iterations));
	}
	
	/**
	 * Returns the function which characterizes the iterations performed in the loop.
	 * @return The function which characterizes the iterations performed in the loop
	 */
	public TimeFunction getIterations() {
		return iterations;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.FlowExecutor)
	 */
	public void request(ElementInstance wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				int iter = Math.round((float)iterations.getValue(wThread));
				if (beforeRequest(wThread) && (iter > 0)) {
					checkList.put(wThread, iter);
					wThread.getElement().addRequestEvent(initialFlow, wThread.getDescendantElementInstance(initialFlow));
				}
				else {
					wThread.cancel(this);
					next(wThread);				
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.FlowExecutor)
	 */
	public void finish(ElementInstance wThread) {
		int iter = checkList.get(wThread);
		if (--iter > 0) {
			wThread.getElement().addRequestEvent(initialFlow, wThread.getDescendantElementInstance(initialFlow));
			checkList.put(wThread, iter);
		}
		else {
			checkList.remove(wThread);
			super.finish(wThread);
		}
	}

}
