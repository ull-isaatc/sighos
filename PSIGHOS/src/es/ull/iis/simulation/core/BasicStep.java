/**
 * 
 */
package es.ull.iis.simulation.core;

import java.util.ArrayDeque;

import es.ull.iis.util.Prioritizable;

/**
 * @author Iván Castilla
 *
 */
public interface BasicStep<AWG extends ActivityWorkGroup, WT extends WorkThread, R extends Resource> extends Describable, Prioritizable, Identifiable {
    /**
     * Searches and returns the WG with the specified identifier.
     * @param wgId The identifier of the searched WG 
     * @return A WG defined in this activity with the specified identifier
     */
	public AWG getWorkGroup(int wgId);
	
	/**
	 * Returns the amount of WGs associated to this activity
	 * @return the amount of WGs associated to this activity
	 */
	public int getWorkGroupSize();
	
	/**
     * Checks if this basic step can be performed with any of its workgroups. Firstly 
     * checks if the basic step is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the basic step cannot be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param wt Work thread wanting to perform the basic step 
     * @return The set of resources which compound the solution. Null if there are not enough
     * resources to carry out the basic step by using this workgroup.
     */
    public ArrayDeque<R> isFeasible(WT wt);

	/**
	 * Checks if the element is valid to perform this basic step.
	 * @param wThread Work thread requesting this basic step
	 * @return True if the element is valid, false in other case.
	 */
	public boolean validElement(WT wThread);
    
	/**
	 * Catches the resources required to carry out this basic step. 
	 * @param wThread Work thread requesting this basic step
	 */
	public void carryOut(WT wThread, ArrayDeque<R> solution);
}
