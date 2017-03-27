/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.LinkedList;
import java.util.Map;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.WorkToken;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.MergeFlowControl;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface MergeFlowEngine {
	Map<Element, MergeFlowControl> getControlStructureInstance();
	Map<Flow, LinkedList<WorkToken>> getGeneralizedBranchesControlInstance();
	
}
