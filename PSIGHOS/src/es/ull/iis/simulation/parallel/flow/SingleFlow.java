/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.Activity;
import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.WorkThread;

/**
 * A flow which executes a single activity. 
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends SingleSuccessorFlow implements TaskFlow, es.ull.iis.simulation.core.flow.SingleFlow {
    /** The activity to be performed */
    protected final Activity act;
    
	/**
	 * Creates a new single flow..
	 * @param simul The simulation this flow belongs to
	 * @param act Activity to be performed
	 */
	public SingleFlow(Simulation simul, Activity act) {
		super(simul);
		this.act = act;
	}

	/**
	 * Obtain the Activity associated to the SingleFlow.
	 * @return The associated Activity.
	 */
	public Activity getActivity() {
		return act;
	}

	/**
	 * Allows a user to add actions carried out when an element is enqueued in an Activity, 
	 * waiting for availables Resources. 
	 * @param e Element requesting this single flow
	 */
	public void inqueue(es.ull.iis.simulation.core.Element e){};
	
	/**
	 * Allows a user to add actions carried out when the element actually starts the
	 * execution of the activity.
	 * @param e Element requesting this single flow
	 */
	public void afterStart(es.ull.iis.simulation.core.Element e){};
	
	@Override
	public void afterFinalize(es.ull.iis.simulation.core.Element e) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.WorkThread)
	 */
	public void request(final WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement()))
					act.request(wThread.getNewWorkItem(this));
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

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.WorkThread)
	 */
	public void finish(final WorkThread wThread) {
		if (act.finish(wThread.getWorkItem())) {
			afterFinalize(wThread.getElement());
			next(wThread);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#addPredecessor(es.ull.iis.simulation.Flow)
	 */
	public void addPredecessor(es.ull.iis.simulation.core.flow.Flow newFlow) {
	}

}

