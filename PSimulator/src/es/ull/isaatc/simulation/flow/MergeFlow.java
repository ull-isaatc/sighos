package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;

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
	/** Indicates if the node is safe or it has to control several triggers for 
	 * the same element through the same incoming branch before reset */ 
	protected final boolean safe;
	
	/**
	 * Create a new MergeFlow intended to be used in a safe context.
	 * @param model Model this flow belongs to
	 */
	public MergeFlow(Model model) {
		this(model, true);
	}

	/**
	 * Create a new MergeFlow which can be used in a safe context or a general one.
	 * @param model Model this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public MergeFlow(Model model, boolean safe) {
		super(model);
		this.safe = safe;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow newFlow) {
		incomingBranches++;
	}
}


