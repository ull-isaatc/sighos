package es.ull.iis.simulation.model.flow;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.FlowExecutor;

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
public abstract class MergeFlow extends SingleSuccessorFlow implements JoinFlow {
	/** Amount of incoming branches */
	protected int incomingBranches;
	/** A structure to control the arrival of incoming branches */
	protected final Map<Element, MergeFlowControl> control = new TreeMap<Element, MergeFlowControl>();
	/** Indicates if the node is safe or it has to control several triggers for 
	 * the same element through the same incoming branch before reset */ 
	protected final boolean safe;
	
	/**
	 * Create a new MergeFlow intended to be used in a safe context.
	 */
	public MergeFlow(Model model) {
		this(model, true);
	}

	/**
	 * Create a new MergeFlow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 */
	public MergeFlow(Model model, boolean safe) {
		super(model);
		this.safe = safe;
	}

	@Override
	public void addPredecessor(Flow newFlow) {
		incomingBranches++;
	}

	/**
	 * Returns how many incoming branches this flow has.
	 * @return How many incoming branches this flow has
	 */
	public int getIncomingBranches() {
		return incomingBranches;
	}

	/**
	 * Returns the safety of this flow. 
	 * @return The safety of this flow
	 */
	public boolean isSafe() {
		return safe;
	}

	@Override
	public void afterFinalize(FlowExecutor fe) {}

	/**
	 * Controls the arrival of an incoming branch.
	 * @param wThread The thread of control of the incoming branch
	 */
	protected void arrive(FlowExecutor wThread) {
		if (!control.containsKey(wThread.getElement()))
			control.put(wThread.getElement(), getNewBranchesControl());
		control.get(wThread.getElement()).arrive(wThread);
	}

	/**
	 * Checks if the last incoming branch can pass.
	 * @param wThread The thread of control of the incoming branch
	 * @return True if the incoming branch activates the outgoing branch; false in other case.
	 */
	protected abstract boolean canPass(FlowExecutor wThread);
	
	/**
	 * Checks if the last incoming branch can reset the control structure. The structure
	 * has to be reset when all the incoming branches has been activated once.
	 * @param wThread The thread of control of the incoming branch
	 * @return True if all the incoming branches were activated once; false in other case.
	 */
	protected boolean canReset(FlowExecutor wThread) {
		return control.get(wThread.getElement()).canReset(incomingBranches);
	}

	/**
	 * Resets the control structure, so the next incoming branch for the same element 
	 * will use a new control structure. 
	 * @param wThread The thread of control of the incoming branch
	 */
	protected void reset(FlowExecutor wThread) {
		if (control.get(wThread.getElement()).reset())
			control.remove(wThread.getElement());
	}
	
	/**
	 * Checks if the control structure has been activated at least once.
	 * @param wThread The thread of control of the incoming branch
	 * @return True if the control structure was activated at least once.
	 */
	protected boolean isActivated(FlowExecutor wThread) {
		return control.get(wThread.getElement()).isActivated();
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.FlowExecutor)
	 */
	public void request(FlowExecutor wThread) {
		final Element elem = wThread.getElement();
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(wThread))
					wThread.cancel(this);
			}
			// FIXME: Fix when parallel is implemented
			//elem.waitProtectedFlow(this);
			arrive(wThread);
			if (canPass(wThread)) {
				control.get(elem).setActivated();
				next(wThread);
			}
			else {
				// If no one of the branches was true, the thread of control must continue anyway
				if (canReset(wThread) && !isActivated(wThread))
					next(wThread.getInstanceSubsequentFlowExecutor(false, this, control.get(elem).getOutgoingFalseToken()));
				wThread.notifyEnd();
			}
			if (canReset(wThread))
				reset(wThread);
			//elem.signalProtectedFlow(this);
		} else
			wThread.notifyEnd();
	}
	
	protected MergeFlowControl getNewBranchesControl() {
		return (safe)? new SafeMergeFlowControl(this) : new GeneralizedMergeFlowControl(this); 
	}

}


