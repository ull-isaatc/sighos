/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.ActivityWorkGroup;

/**
 * A class that executes a flow
 * @author Ivan Castilla Rodriguez
 *
 */
public interface FlowExecutor extends TimeFunctionParams {
	ActivityWorkGroup getModelWG();
}
