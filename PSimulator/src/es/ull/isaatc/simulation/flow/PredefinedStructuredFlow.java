/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;


/**
 * An structured flow with predefined both entry and exit points.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class PredefinedStructuredFlow extends StructuredFlow {

	/**
	 * Creates a new structured flow with predefined entry and exit points.
	 * @param model Model this flow belongs to.
	 */
	public PredefinedStructuredFlow(Model model) {
		super(model);
	}
	
	/**
	 * Adds a new branch starting in <code>initialBranch</code> and finishing in <code>finalBranch</code>.
	 * The <code>initialFlow</code> is linked to the <code>initialBranch</code> whereas
	 * the <code>final Branch</code> is linked to the <code>finalFlow</code> 
	 * @param initialBranch First step of the internal branch
	 * @param finalBranch Last step of the internal branch
	 */
	public void addBranch(InitializerFlow initialBranch, FinalizerFlow finalBranch) {
		initialBranch.setRecursiveStructureLink(this);
		initialFlow.link(initialBranch);
		finalBranch.link(finalFlow);		
	}
	
	/**
	 * Adds a new branch consisting of a unique flow. The <code>branch</code> has the
	 * <code>initialFlow</code> as predecessor and the <code>finalFlow</code> as successor. 
	 * @param branch A unique flow defining an internal branch
	 */
	public void addBranch(TaskFlow branch) {
		addBranch(branch, branch);		
	}

}
