/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Activity;
import es.ull.isaatc.simulation.model.Model;

/**
 * A flow which executes a single activity. 
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends SingleSuccessorFlow implements TaskFlow {
    /** The activity to be performed */
    protected Activity act;
    
	/**
	 * Creates a new single flow..
	 * @param model Model this flow belongs to
	 * @param act Activity to be performed
	 */
	public SingleFlow(Model model, Activity act) {
		super(model);
		this.act = act;
		userMethods.put("afterFinalize", "");
		userMethods.put("inqueue", "");
		userMethods.put("afterStart", "");
		
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
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow newFlow) {
	}
	
}

