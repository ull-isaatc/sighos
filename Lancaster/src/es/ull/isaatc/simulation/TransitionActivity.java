/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.EnumSet;

/**
 * A special kind of activity, whose workgroups are considered states of a transition 
 * matrix. Once an element requests this activity, it starts going from one workgroup
 * to another one until it reaches the final transition.
 * @author Iván Castilla Rodríguez
 *
 */
public class TransitionActivity extends Activity {
	protected TransitionMatrix<Activity.WorkGroup> transMatrix;
	/** The last transition. Indicates the element have actually finished the activity. */ 
	public Activity.WorkGroup finalTransition = new FinalWorkGroup();
	/** The first transition. The starting point of the element. */
	public Activity.WorkGroup initialTransition = new InitialWorkGroup();
	
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
	 * @param modifiers
	 */
	public TransitionActivity(int id, Simulation simul, String description, EnumSet<Modifier> modifiers) {
		this(id, simul, description, 0, modifiers);
	}

	/**
	 * @param id
	 * @param simul
	 * @param description
	 * @param priority
	 * @param modifiers
	 */
	public TransitionActivity(int id, Simulation simul, String description,
			int priority, EnumSet<Modifier> modifiers) {
		super(id, simul, description, priority, modifiers);
		transMatrix = new TransitionMatrix<WorkGroup>();
		transMatrix.add(initialTransition);
		transMatrix.add(finalTransition);
	}

	/**
	 * 
	 * @param sf
	 * @return
	 */
    protected boolean isFeasible(SingleFlow sf) {
    	if (!stillFeasible)
    		return false;
    	
    	// We copy the row to change contents without modifying original
    	TransitionMatrix<Activity.WorkGroup>.TransitionRow tr = null;
    	try {
    		tr = transMatrix.getRow(sf.getExecutionWG());
    	} catch(NullPointerException e) {
        	if (transMatrix == null)
        		System.err.println(sf + "\tTRANSMATRIX");
        	else if (sf.getExecutionWG() == null)
        		System.err.println(sf + "\tEXECUTIONWG");
    	}
		
		Activity.WorkGroup wg = null;
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
			else if (wg.isFeasible(sf)) {
                sf.setExecutionWG(wg);
                if (!isNonPresential())
                	sf.getElement().setCurrentSF(sf);
//                System.out.println(sf.elem + "\t" + sf.elem.ts + "\tTRANS\tGO\t" + wg.description);
				debug("Can be carried out by\t" + sf.getElement().getIdentifier() + "\t" + wg);
                gotOne = true;
            }
			// The transition is valid but not feasible. Re-adjust to find next one.
			else
				tr.reAdjust(wg);
		}
        return true;
    }
    
    public void addTransition(Activity.WorkGroup fromTrans, Activity.WorkGroup toTrans, double prob) {
    	transMatrix.add(fromTrans, toTrans, prob);
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
			Activity.WorkGroup fromTrans = getWorkGroup(i - 1);
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
	
    public class InitialWorkGroup extends Activity.WorkGroup {
    	public InitialWorkGroup() {
    		super(-1, null, 0);
    		description = "Initial Transition";
    	}
    }
    
    public class FinalWorkGroup extends Activity.WorkGroup {
    	public FinalWorkGroup() {
    		super(-2, null, 0);
    		description = "Final Transition";
    	}
    }
}
