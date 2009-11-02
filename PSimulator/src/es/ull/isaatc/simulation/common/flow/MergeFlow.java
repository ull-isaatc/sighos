package es.ull.isaatc.simulation.common.flow;


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
public interface MergeFlow extends SingleSuccessorFlow, JoinFlow {
	/**
	 * @return the safe
	 */
	public boolean isSafe();

	/**
	 * @return the incomingBranches
	 */
	public int getIncomingBranches();
}


