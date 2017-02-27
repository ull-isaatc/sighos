/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.FlowExecutor;
import es.ull.iis.function.TimeFunction;

/**
 * A structured loop flow which resembles a for loop. The internal flow is
 * executed n times. n is defined by using the <code>iterations</code> attribute.
 * Be careful when using a continuous random distribution for defining the number
 * of iterations, since decimal values are rounded to the closest integer.  
 * @author Iván Castilla Rodríguez
 */
public class ForLoopFlow extends StructuredLoopFlow implements es.ull.iis.simulation.core.flow.ForLoopFlow {
	/** Loop iterations */
	protected final TimeFunction iterations; 
	/** List used by the control system. */
	protected final SortedMap<FlowExecutor, Integer> checkList;

	/**
	 * Create a new ForLoopFlow.
	 * @param simul Simulation this flow belongs to
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Simulation simul, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, TimeFunction iterations) {
		super(simul, initialSubFlow, finalSubFlow);
		this.iterations = iterations;
		checkList = Collections.synchronizedSortedMap(new TreeMap<FlowExecutor, Integer>());
	}
	
	/**
	 * Create a new ForLoopFlow.
	 * @param simul Simulation this flow belongs to
	 * @param subFlow A unique flow defining an internal subflow
	 * @param iterations Loop iterations.
 	 */
	public ForLoopFlow(Simulation simul, TaskFlow subFlow, TimeFunction iterations) {
		this(simul, subFlow, subFlow, iterations);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.WorkThread)
	 */
	public void request(FlowExecutor wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				int iter = Math.round((float)iterations.getValue(wThread.getElement()));
				if (beforeRequest(wThread.getElement()) && (iter > 0)) {
					checkList.put(wThread, iter);
					wThread.getInstanceDescendantWorkThread().requestFlow(initialFlow);
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
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.WorkThread)
	 */
	public void finish(FlowExecutor wThread) {
		int iter = checkList.get(wThread);
		if (--iter > 0) {
			wThread.getInstanceDescendantWorkThread().requestFlow(initialFlow);
			checkList.put(wThread, iter);
		}
		else {
			checkList.remove(wThread);
			afterFinalize(wThread.getElement());
			next(wThread);
		}
	}

	@Override
	public TimeFunction getIterations() {
		return iterations;
	}

}
