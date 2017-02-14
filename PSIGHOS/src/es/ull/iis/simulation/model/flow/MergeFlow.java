package es.ull.iis.simulation.model.flow;

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
	 */
	public MergeFlow() {
		this(true);
	}

	/**
	 * Create a new MergeFlow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 */
	public MergeFlow(boolean safe) {
		super();
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

}


