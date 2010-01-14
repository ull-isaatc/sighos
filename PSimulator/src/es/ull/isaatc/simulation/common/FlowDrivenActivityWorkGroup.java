/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.common.flow.FinalizerFlow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface FlowDrivenActivityWorkGroup {

	InitializerFlow getInitialFlow();
	FinalizerFlow getFinalFlow();
}
