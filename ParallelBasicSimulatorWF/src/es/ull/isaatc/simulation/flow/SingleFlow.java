/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.Element;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.WorkThread;

/**
 * A flow which executes a single activity. 
 * @author Iv�n Castilla Rodr�guez
 */
public class SingleFlow extends SingleSuccessorFlow implements TaskFlow {
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
	public void inqueue(Element e){};
	
	/**
	 * Allows a user to add actions carried out when the element actually starts the
	 * execution of the activity.
	 * @param e Element requesting this single flow
	 */
	public void afterStart(Element e){};
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.TaskFlow#afterFinalize(es.ull.isaatc.simulation.Element)
	 */
	public void afterFinalize(Element e) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement()))
					wThread.getElement().addRequestActivityEvent(wThread.getNewWorkItem(this));
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
	public void addPredecessor(Flow newFlow) {
	}
	
}
