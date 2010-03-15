/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtra3Phase.flow;

import es.ull.isaatc.simulation.groupedExtra3Phase.Activity;
import es.ull.isaatc.simulation.groupedExtra3Phase.Simulation;
import es.ull.isaatc.simulation.groupedExtra3Phase.WorkThread;

/**
 * A flow which executes a single activity. 
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends SingleSuccessorFlow implements TaskFlow, es.ull.isaatc.simulation.common.flow.SingleFlow {
    /** The activity to be performed */
    protected Activity act;
    
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
	 * Set a new Activity associated to the SingleFlow.
	 * @param act The new Activity.
	 */
	public void setActivity(Activity act) {
		this.act = act;
	}
	
	/**
	 * Allows a user to add actions carried out when an element is enqueued in an Activity, 
	 * waiting for availables Resources. 
	 * @param e Element requesting this single flow
	 */
	public void inqueue(es.ull.isaatc.simulation.common.Element e){};
	
	/**
	 * Allows a user to add actions carried out when the element actually starts the
	 * execution of the activity.
	 * @param e Element requesting this single flow
	 */
	public void afterStart(es.ull.isaatc.simulation.common.Element e){};
	
	@Override
	public void afterFinalize(es.ull.isaatc.simulation.common.Element e) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
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
	 * @see es.ull.isaatc.simulation.TaskFlow#finish(es.ull.isaatc.simulation.WorkThread)
	 */
	public void finish(WorkThread wThread) {
		if (act.finish(wThread.getWorkItem())) {
			afterFinalize(wThread.getElement());
			next(wThread);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(es.ull.isaatc.simulation.common.flow.Flow newFlow) {
	}

}

