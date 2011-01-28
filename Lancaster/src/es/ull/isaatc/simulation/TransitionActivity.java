/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.EnumSet;

import es.ull.isaatc.simulation.info.ElementActionInfo;
import es.ull.isaatc.util.RandomPermutation;

/**
 * A special kind of activity, whose workgroups are considered states of a transition 
 * matrix. Once an element requests this activity, it starts going from one workgroup
 * to another one until it reaches the final transition.
 * @author Iván Castilla Rodríguez
 *
 */
public class TransitionActivity extends TimeDrivenActivity {
	protected TransitionMatrix<ActivityWorkGroup> transMatrix;
	/** The last transition. Indicates the element have actually finished the activity. */ 
	private final ActivityWorkGroup finalTransition;
	/** The first transition. The starting point of the element. */
	private final ActivityWorkGroup initialTransition;
	
	/**
	 * @param id
	 * @param simul
	 * @param description
	 */
	public TransitionActivity(int id, Simulation simul, String description) {
		this(id, simul, description, 0, EnumSet.noneOf(Modifier.class));
	}

	/**
	 * @param id
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public TransitionActivity(int id, Simulation simul, String description, int priority) {
		this(id, simul, description, priority, EnumSet.noneOf(Modifier.class));
	}

	/**
	 * @param id
	 * @param simul
	 * @param description
	 */
	public TransitionActivity(int id, Simulation simul, String description, EnumSet<Modifier> modifiers) {
		this(id, simul, description, 0, modifiers);
	}

	/**
	 * @param id
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public TransitionActivity(int id, Simulation simul, String description, int priority, EnumSet<Modifier> modifiers) {
		super(id, simul, description, priority, modifiers);
		finalTransition = new ActivityWorkGroup(-2, new SimulationTimeFunction(simul, "ConstantVariate", 0), 0);
		initialTransition = new ActivityWorkGroup(-1, new SimulationTimeFunction(simul, "ConstantVariate", 0), 0);
		transMatrix = new TransitionMatrix<ActivityWorkGroup>();
		transMatrix.add(initialTransition);
		transMatrix.add(finalTransition);
	}

	@Override
	public boolean finish(WorkItem wItem) {
		Element elem = wItem.getElement();
		if (elem.isDebugEnabled())
			elem.debug("Requests Transition to\t" + this + "\t" + getDescription());
		
		// Beginning MUTEX access to activity manager
		manager.waitSemaphore();

        elem.waitSemaphore();
		ArrayList<ActivityManager> amList = null;
		if (isFeasible(wItem)) { 
			amList = wItem.releaseCaughtResources();

			if (wItem.getExecutionWG() == finalTransition) {
				if (!isNonPresential())
					elem.setCurrent(null);				
			}
			else {
				// Resets the time left counter not to be considered an interruptible activity
				wItem.setTimeLeft(Double.NaN);
				carryOut(wItem);
			}
		}
		// There are not possible transitions. The element must stay in the same activity with the same workgroup
		else {
			double auxTs = elem.getTs() + ((ActivityWorkGroup)wItem.getExecutionWG()).getDuration();
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
			elem.debug("Re-starts\t" + this + "\t" + getDescription());
			elem.addEvent(elem.new FinishFlowEvent(auxTs, wItem.getFlow(), wItem.getWorkThread()));
		}
    	elem.signalSemaphore();
		
		// Ending MUTEX access to activity manager
		manager.signalSemaphore();

		// Resources from the previous transition were freed
		if (amList != null) {
			int[] order = RandomPermutation.nextPermutation(amList.size());
			for (int ind : order) {
				ActivityManager am = amList.get(ind);
				am.waitSemaphore();
				am.availableResource();
				am.signalSemaphore();
			}
			// Only if there are no more transitions
			if (elem.getCurrent() == null) {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes\t" + this + "\t" + description);
				// Checks if there are pending activities that haven't noticed the
				// element availability
				if (!isNonPresential())
					elem.addAvailableElementEvents();
				return true;
			}
		}

        return false;
	}

	@Override
	public void request(WorkItem wItem) {
		// Since this method is invoked once per WorkItem, the initial transition is set here
		wItem.setExecutionWG(initialTransition);
		super.request(wItem);
	}

	@Override
    protected boolean isFeasible(WorkItem wi) {
    	if (!stillFeasible)
    		return false;
    	
    	// We copy the row to change contents without modifying original
    	TransitionMatrix<ActivityWorkGroup>.TransitionRow tr = null;
    	try {
    		tr = transMatrix.getRow((ActivityWorkGroup)wi.getExecutionWG());
    	} catch(NullPointerException e) {
        	if (transMatrix == null)
        		System.err.println(wi + "\tTRANSMATRIX");
        	else if (wi.getExecutionWG() == null)
        		System.err.println(wi + "\tEXECUTIONWG");
    	}
		
		ActivityWorkGroup wg = null;
		boolean gotOne = false;
		double prob = Math.random();
		while(!gotOne) {
			wg = tr.getNext(prob);
			// There is no valid transition from this point on. It must remain in the
			// current transition
			if (wg == null) {
//                System.out.println(sf.elem + "\t" + sf.elem.ts + "\tTRANS\tSTAY\t" + sf.executionWG.description);
                // FIXME: It doesn't works with transitions
//		        stillFeasible = false;
		        return false;
			}
			// The transition is valid and feasible
			else if (wg.isFeasible(wi)) {
                wi.setExecutionWG(wg);
                if (!isNonPresential())
                	wi.getElement().setCurrent(wi);
//                System.out.println(sf.elem + "\t" + sf.elem.ts + "\tTRANS\tGO\t" + wg.description);
				debug("Can be carried out by\t" + wi.getElement().getIdentifier() + "\t" + wg);
                gotOne = true;
            }
			// The transition is valid but not feasible. Re-adjust to find next one.
			else
				tr.reAdjust(wg);
		}
        return true;
    }
    
    public void addTransition(ActivityWorkGroup fromTrans, ActivityWorkGroup toTrans, double prob) {
    	transMatrix.add(fromTrans, toTrans, prob);
    }

    public void addInitialTransition(ActivityWorkGroup toTrans, double prob) {
    	transMatrix.add(initialTransition, toTrans, prob);
    }

    public void addFinalTransition(ActivityWorkGroup fromTrans, double prob) {
    	transMatrix.add(fromTrans, finalTransition, prob);
    }

    public void addInitialFinalTransition(double prob) {
    	transMatrix.add(initialTransition, finalTransition, prob);
    }
    /**
     * Adds the transitions as indicated in the matrix[N,N], where N = number of workgroups + 1. 
     * Rows represents sources and columns are destinations. Therefore, column 0 is wg 0, 
     * column 1 is wg1, and so on; column N is FinalWorkGroup. Analogously, row 0 is 
     * InitialWorkGroup, row 1 is wg0, and so on.  
     * @param trans
     */
	public void addTransitions(double[][] trans) {
		int nWG = workGroupTable.size();
		for (int j = 0; j < nWG; j++) {
			if (trans[0][j] > 0.0)
				addTransition(initialTransition, getWorkGroup(j), trans[0][j]);
		}
		if (trans[0][nWG] > 0.0)
			addTransition(initialTransition, finalTransition, trans[0][nWG]);
		for (int i = 1; i < nWG + 1; i++) {
			ActivityWorkGroup fromTrans = getWorkGroup(i - 1);
			for (int j = 0; j < nWG; j++) {
				if (trans[i][j] > 0.0)
					addTransition(fromTrans, getWorkGroup(j), trans[i][j]);
			}
			if (trans[i][nWG] > 0.0)
				addTransition(fromTrans, finalTransition, trans[i][nWG]);
		}			
	}
    
	public boolean checkTransitions() {
		return transMatrix.checkTransitions(initialTransition, finalTransition);
	}
	
}
