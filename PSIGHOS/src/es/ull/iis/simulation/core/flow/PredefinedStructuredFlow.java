/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link StructuredFlow} with predefined both entry and exit points.
 * @author Iván Castilla Rodríguez
 *
 */
public interface PredefinedStructuredFlow<WT extends WorkThread<?>> extends StructuredFlow<WT> {
	/**
	 * Adds a new branch starting in <tt>initialBranch</tt> and finishing in <tt>finalBranch</tt>.
	 * The entry point is linked to the <tt>initialBranch</tt> whereas
	 * the <tt>final Branch</tt> is linked to the exit point. 
	 * @param initialBranch First step of the internal branch
	 * @param finalBranch Last step of the internal branch
	 */
	public void addBranch(InitializerFlow<WT> initialBranch, FinalizerFlow<WT> finalBranch);
	
	/**
	 * Adds a new branch consisting of a unique flow. The <tt>branch</tt> has the
	 * entry point as predecessor and the exit point as successor. 
	 * @param branch A unique flow defining an internal branch
	 */
	public void addBranch(TaskFlow<WT> branch);

}
