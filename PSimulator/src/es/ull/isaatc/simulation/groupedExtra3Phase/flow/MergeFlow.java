package es.ull.isaatc.simulation.groupedExtra3Phase.flow;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import es.ull.isaatc.simulation.groupedExtra3Phase.Element;
import es.ull.isaatc.simulation.groupedExtra3Phase.Simulation;
import es.ull.isaatc.simulation.groupedExtra3Phase.WorkThread;

/**
 * A flow which merges several incoming branches into a single outgoing branch. The incoming 
 * branches are handled in an internal structure and managed per element.<p> When an incoming 
 * branch arrives, the <code>arrive</code> is invoked. Both, true and false work threads 
 * are computed. Then, the flow checks (depending on the implementation) if this branch 
 * <code>canPass</code> or not. If it can pass, the outgoing flow is activated. Next, 
 * the flow checks if the branch <code>canReset</code> the structure, if so, it's reset.
 * Only true work threads can pass, but both true and false threads can reset the structure.
 * In case the structure has to be reset and it has not been activated, a new false work thread
 * continues the execution.
 * @author ycallero
 */
public abstract class MergeFlow extends SingleSuccessorFlow implements JoinFlow, es.ull.isaatc.simulation.common.flow.MergeFlow {
	/** Amount of incoming branches */
	protected int incomingBranches;
	/** A structure to control the arrival of incoming branches */
	protected final Map<Element, MergeFlowControl> control;
	// FIXME Una posible mejora es hacer que haya factorías de los MergeFlowControl, de forma
	// que sólo se pregunte por el safe una vez.
	/** Indicates if the node is safe or it has to control several triggers for 
	 * the same element through the same incoming branch before reset */ 
	protected final boolean safe;
	
	/**
	 * Create a new MergeFlow intended to be used in a safe context.
	 * @param simul Simulation this flow belongs to
	 */
	public MergeFlow(Simulation simul) {
		this(simul, true);
	}

	/**
	 * Create a new MergeFlow which can be used in a safe context or a general one.
	 * @param simul Simulation this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public MergeFlow(Simulation simul, boolean safe) {
		super(simul);
		control = Collections.synchronizedSortedMap(new TreeMap<Element, MergeFlowControl>());
		this.safe = safe;
	}

	/**
	 * Controls the arrival of an incoming branch.
	 * @param wThread The thread of control of the incoming branch
	 */
	protected void arrive(WorkThread wThread) {
		if (!control.containsKey(wThread.getElement()))
			control.put(wThread.getElement(), getNewBranchesControl());
		control.get(wThread.getElement()).arrive(wThread);
	}

	/**
	 * Checks if the last incoming branch can pass.
	 * @param wThread The thread of control of the incoming branch
	 * @return True if the incoming branch activates the outgoing branch; false in other case.
	 */
	protected abstract boolean canPass(WorkThread wThread);
	
	/**
	 * Checks if the last incoming branch can reset the control structure. The structure
	 * has to be reset when all the incoming branches has been activated once.
	 * @param wThread The thread of control of the incoming branch
	 * @return True if all the incoming branches were activated once; false in other case.
	 */
	protected boolean canReset(WorkThread wThread) {
		return control.get(wThread.getElement()).canReset(incomingBranches);
	}

	/**
	 * Resets the control structure, so the next incoming branch for the same element 
	 * will use a new control structure. 
	 * @param wThread The thread of control of the incoming branch
	 */
	protected void reset(WorkThread wThread) {
		if (control.get(wThread.getElement()).reset())
			control.remove(wThread.getElement());
	}
	
	/**
	 * Checks if the control structure has been activated at least once.
	 * @param wThread The thread of control of the incoming branch
	 * @return True if the control structure was activated at least once.
	 */
	protected boolean isActivated(WorkThread wThread) {
		return control.get(wThread.getElement()).isActivated();
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		final Element elem = wThread.getElement();
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(elem))
					wThread.cancel(this);
			}
			
			elem.waitProtectedFlow(this);
			arrive(wThread);
			if (canPass(wThread)) {
				control.get(elem).setActivated();
				next(wThread);
			}
			else {
				// If no one of the branches was true, the thread of control must continue anyway
				if (canReset(wThread) && !isActivated(wThread))
					next(wThread.getInstanceSubsequentWorkThread(false, this, control.get(elem).getOutgoingFalseToken()));
				wThread.notifyEnd();
			}
			if (canReset(wThread))
				reset(wThread);
			elem.signalProtectedFlow(this);
		} else
			wThread.notifyEnd();
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(es.ull.isaatc.simulation.common.flow.Flow newFlow) {
		incomingBranches++;
	}

	protected MergeFlowControl getNewBranchesControl() {
		return (safe)? new SafeMergeFlowControl(this) : new GeneralizedMergeFlowControl(this); 
	}

	@Override
	public int getIncomingBranches() {
		return incomingBranches;
	}

	@Override
	public boolean isSafe() {
		return safe;
	}
	
}


