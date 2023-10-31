/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

/**
 * @author Iván Castilla
 *
 */
public class ProbabilityDistributionExpressionWrapper {

	/**
	 * 
	 */
	public ProbabilityDistributionExpressionWrapper(OSDiWrapper wrap, String instanceId) {
		final Set<String> superclasses = wrap.getClassesForIndividual(instanceId);
	}

}
