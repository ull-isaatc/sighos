/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;

/**
 * A class that executes a flow
 * @author Ivan Castilla Rodriguez
 *
 */
public interface FlowExecutor extends TimeFunctionParams {
	ElementType getType();
	Element getModelElement();
}
