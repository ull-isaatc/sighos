/**
 * 
 */
package es.ull.isaatc.simulation.sequential.flow;

import java.util.SortedMap;
import java.util.TreeMap;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.WorkThread;

/**
 * A structured loop flow which resembles a for loop. The internal flow is
 * executed n times. n is defined by using the <code>iterations</code> attribute.
 * Be careful when using a continuous random distribution for defining the number
 * of iterations, since decimal values are rounded to the closest integer.  
 * @author Iv�n Castilla Rodr�guez
 */
public class ForLoopFlow extends StructuredLoopFlow implements es.ull.isaatc.simulation.common.flow.ForLoopFlow {
	/** Loop iterations */
	protected final TimeFunction iterations; 
	/** List used by the control system. */
	protected final SortedMap<WorkThread, Integer> checkList;

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
		checkList = new TreeMap<WorkThread, Integer>();
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
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				int iter = Math.round((float)iterations.getPositiveValue(wThread.getElement().getTs()));
				if (beforeRequest(wThread.getElement()) && (iter > 0)) {
					checkList.put(wThread, iter);
					wThread.getElement().addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));
				}
				else {
					wThread.setExecutable(false, this);
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
	 * @see es.ull.isaatc.simulation.TaskFlow#finish(es.ull.isaatc.simulation.WorkThread)
	 */
	public void finish(WorkThread wThread) {
		int iter = checkList.get(wThread);
		if (--iter > 0) {
			wThread.getElement().addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));
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